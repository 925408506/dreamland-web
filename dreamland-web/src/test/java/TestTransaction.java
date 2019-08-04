
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import wang.dreamland.www.entity.User;
import wang.dreamland.www.service.UserService;

@ContextConfiguration(locations = {"classpath:spring-mybatis.xml","classpath:spring-mvc.xml"})
public class TestTransaction  extends AbstractJUnit4SpringContextTests {
  @Autowired
    private UserService userService;
    @Test
    public void testSave(){
        try {
            User user = new User();
            user.setNickName("一刀修罗");
            user.setEmail("123@qq.com");
            userService.regist(user);
        }catch (Exception e){
            System.out.println("by zero");
            e.getStackTrace();
        }
    }

}
