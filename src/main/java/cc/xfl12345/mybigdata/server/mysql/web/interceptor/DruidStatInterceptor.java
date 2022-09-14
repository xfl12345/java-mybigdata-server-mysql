package cc.xfl12345.mybigdata.server.mysql.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 拦截 Druid 监视器请求。限制访问。
 */
@Slf4j
public class DruidStatInterceptor implements HandlerInterceptor {

    /**
     * 拦截请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
//        System.out.println("拦截请求");
        return true;
    }

    /**
     * 拦截响应
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
//        System.out.println("拦截响应");
    }

    /**
     * 拦截渲染
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
//        System.out.println("拦截渲染");
    }
}
