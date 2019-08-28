package wang.dreamland.www.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import wang.dreamland.www.common.Constants;
import wang.dreamland.www.common.MD5Util;
import wang.dreamland.www.common.RandStringUtils;
import wang.dreamland.www.entity.User;
import wang.dreamland.www.service.UserService;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by wly on 2018/4/21.
 */
@Controller
public class LoginController extends BaseController {
    private final static Logger log = Logger.getLogger( LoginController.class);
    // jdbcTemplate HibernateTemplate
    @Autowired
    private UserService userService;
    @Autowired// redis数据库操作模板
    private RedisTemplate<String, String> redisTemplate;
    // mq消息模板.
    @Autowired
    @Qualifier("jmsQueueTemplate")
    private JmsTemplate jmsTemplate;

    @RequestMapping("/login")
    public String login(Model model) {
        User user = (User)getSession().getAttribute("user");
        if(user!=null){
            return "/personal/personal";
        }
        return "../login";
    }

    /**
     * 用户登录
     * @param model
     * @param email
     * @param password
     * @param code
     * @param telephone
     * @param phone_code
     * @param state
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/doLogin")
    public String doLogin(Model model, @RequestParam(value = "username",required = false) String email,
                          @RequestParam(value = "password",required = false) String password,
                          @RequestParam(value = "code",required = false) String code,
                          @RequestParam(value = "telephone",required = false) String telephone,
                          @RequestParam(value = "phone_code",required = false) String phone_code,
                          @RequestParam(value = "state",required = false) String state,
                          @RequestParam(value = "pageNum",required = false) Integer pageNum ,
                          @RequestParam(value = "pageSize",required = false) Integer pageSize) {

        //判断是否是手机登录
        if (StringUtils.isNotBlank(telephone)) {
            //手机登录
            //从redis获取验证码
            String yzm = redisTemplate.opsForValue().get( telephone );
            if(phone_code.equals(yzm)){
                //验证码正确
                User user = userService.findByPhone(telephone);
                getSession().setAttribute("user", user);
                model.addAttribute("user", user);
                log.info("手机快捷登录成功");
                return "/list";

            }else {
                //验证码错误或过期
                model.addAttribute("error","phone_fail");
                return  "../login";
            }

        } else {
            //账号登录

        if (StringUtils.isBlank(code)) {
            model.addAttribute("error", "fail");
            return "../login";
        }
        int b = checkValidateCode(code);
        if (b == -1) {
            model.addAttribute("error", "fail");
            return "../login";
        } else if (b == 0) {
            model.addAttribute("error", "fail");
            return "../login";
        }
        password = new Md5PasswordEncoder().encodePassword(password, email);
        User user = userService.login(email, password);
        if (user != null) {
            if ("0".equals(user.getState())) {
                //未激活
                model.addAttribute("email", email);
                model.addAttribute("error", "active");
                return "../login";
            }
            log.info("用户登录登录成功");
            getSession().setAttribute("user", user);
            model.addAttribute("user", user);
            return "redirect:/list";
        } else {
            log.info("用户登录登录失败");
            model.addAttribute("email", email);
            model.addAttribute("error", "fail");
            return "../login";
        }

    }

    }

    // 匹对验证码的正确性
    public int checkValidateCode(String code) {
        Object vercode = getRequest().getSession().getAttribute("VERCODE_KEY");
        if (null == vercode) {
            return -1;
        }
        if (!code.equalsIgnoreCase(vercode.toString())) {
            return 0;
        }
        return 1;
    }

    /**
     * 发送手机验证码
     * @param model
     * @param telephone
     * @return
     */
    @RequestMapping("/sendSms")
    @ResponseBody
    public Map<String,Object> index(Model model, @RequestParam(value = "telephone",required = false) final String telephone ) {
        Map map = new HashMap<String,Object>();
        try { //  发送验证码操作
            final String code = RandStringUtils.getCode();
            // 5分钟 有效 redis保存验证码
            redisTemplate.opsForValue().set(telephone, code, 5, TimeUnit.MINUTES);
            log.debug("--------短信验证码为："+code);
            // 调用ActiveMQ jmsTemplate，发送一条消息给MQ
            jmsTemplate.send("login_msg", new MessageCreator() {
                @Override
                public Message createMessage(javax.jms.Session session) throws JMSException {
                    MapMessage mapMessage = session.createMapMessage();
                    mapMessage.setString("telephone",telephone);
                    mapMessage.setString("code", code);
                    return mapMessage;
                }
            });
            map.put( "msg",true );
        } catch (Exception e) {
            e.printStackTrace();
            map.put( "msg",false );
        }

        return map;

    }

    @RequestMapping("/loginout")
    public String exit (Model model){
        log.info("------退出登录------");
        getSession().removeAttribute("user");
        getSession().invalidate();
        return "../login";
    }
}
