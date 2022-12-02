package com.xiao.pay.payalibaba.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author aloneMan
 * @projectName pay
 * @createTime 2022-11-26 12:26:20
 * @description
 */
@WebFilter(urlPatterns = "/*", filterName = "corsFilter")
@Component
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        /*
         * 跨域设置允所有请求跨域
         * 如果允许指定的客户端跨域设置: http://127.0.0.1:8020
         */
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        if (((HttpServletRequest) req).getMethod().equals("OPTIONS")) {
            response.getWriter().println("ok");
            return;
        }

        chain.doFilter(req, res);
    }
}
