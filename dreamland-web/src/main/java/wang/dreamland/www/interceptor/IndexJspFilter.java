package wang.dreamland.www.interceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import wang.dreamland.www.common.PageHelper;
import wang.dreamland.www.dao.UserContentMapper;
import wang.dreamland.www.entity.UserContent;
import javax.servlet.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by wly on 2018/4/26.
 */
public class IndexJspFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("===========自定义过滤器==========");
        ServletContext context = request.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
        UserContentMapper userContentMapper = ctx.getBean(UserContentMapper.class);
        //开始分页
        PageHelper.startPage(null, null);
        List<UserContent> list =  userContentMapper.select( null );
        //分页结束
        PageHelper.Page endPage = PageHelper.endPage();
        request.setAttribute("page", endPage );
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
