package org.example.reggie.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.BaseContext;
import org.example.reggie.common.R;
import org.example.reggie.entity.Employee;
import org.example.reggie.entity.User;
import org.example.reggie.utils.IpUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 登录检查过滤器
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 获取请求路径
        String requestURI = request.getRequestURI();
        if (!requestURI.contains("/backend/") && !requestURI.contains("/front/") && !requestURI.contains(".html")) {

            log.info("=================================");
            log.info("*********************************");
            log.info("*             新请求             *");
            log.info("* 请求时间：\t{}  *", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(System.currentTimeMillis()));
            log.info("* 请求者IP：\t{}\t *", IpUtil.getIpAddr(request));
            log.info("* 请求者端口：\t{}\t\t\t *", request.getRemotePort());
            log.info("*********************************");

            log.info("请求路径：{}", requestURI);
        }


        // 判断是否是登录请求
        if (requestURI.contains("/employee/login") || requestURI.contains("/user/login")) {
            // 放行
            log.info("登录请求，放行");
            filterChain.doFilter(request, response);
            return;
        }

        if (requestURI.contains("/user/sendMsg")) {
            // 放行
            log.info("发送验证码请求，放行");
            filterChain.doFilter(request, response);
            return;
        }

        // 判断是否是静态资源
        if (requestURI.contains("/backend/") || requestURI.contains("/front/") || requestURI.contains(".html")) {
            // 放行
            // log.info("静态资源，放行");
            filterChain.doFilter(request, response);
            return;
        }


        // 判断是否登录
        //后台管理
        if (request.getSession().getAttribute("employee") != null) {

            log.info("后台管理登录验证");

            // 获取当前登录账号
            Employee employee = (Employee) request.getSession().getAttribute("employee");

            // 将当前登录账号存入ThreadLocal
            log.info("当前登录账号为员工账号：{}", employee.getId());
            BaseContext.setCurrentId(String.valueOf(employee.getId()));

            // 放行
            log.info("已登录，放行");
            filterChain.doFilter(request, response);

            return;
        }

        // 客户端
        if (request.getSession().getAttribute("user") != null) {

            log.info("客户端登录验证");

            // 获取当前登录账号
            User user = (User) request.getSession().getAttribute("user");

            // 将当前登录账号存入ThreadLocal
            log.info("当前登录账号为用户账号：{}", user.getId());
            BaseContext.setCurrentId(String.valueOf(user.getId()));

            // 放行
            log.info("已登录，放行");
            filterChain.doFilter(request, response);

            return;
        }

        // 未登录
        log.info("未登录，重定向到登录页面");
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }
}
