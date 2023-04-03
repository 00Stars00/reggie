package org.example.reggie.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.reggie.common.BaseContext;
import org.example.reggie.common.R;
import org.example.reggie.entity.Employee;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        log.info("请求路径：{}", requestURI);

        // 判断是否是登录请求
        if (requestURI.contains("/employee/login")) {
            // 放行
            log.info("登录请求，放行");
            filterChain.doFilter(request, response);
            return;
        }

        // 判断是否是静态资源
        if (requestURI.contains("/backend/") || requestURI.contains(".html")) {
            // 放行
            log.info("静态资源，放行");
            filterChain.doFilter(request, response);
            return;
        }

        // 判断是否是登录页面
        if (requestURI.contains("/backend/page/login/login.html")) {
            // 放行
            log.info("登录页面，放行");
            filterChain.doFilter(request, response);
            return;
        }

        // 判断是否登录
        Employee employee = (Employee) request.getSession().getAttribute("employee");
        if (employee == null) {
            // 未登录
            log.info("未登录，重定向到登录页面");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return;
        }

        // 已登录
        log.info("已登录，放行");
        log.info("目前登录账号: {}", JSON.toJSONString(employee.getUsername()));

        log.info("线程名: {}", Thread.currentThread().getName());
        log.info("线程ID: {}", Thread.currentThread().getId());

        // 将当前登录账号存入ThreadLocal
        BaseContext.setCurrentId(String.valueOf(employee.getId()));


        filterChain.doFilter(request, response);
    }
}
