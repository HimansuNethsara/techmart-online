package com.techmart.metrics;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class PerformanceMonitoringFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        if (isExcluded(path)) {
            chain.doFilter(request, response);
            return;
        }

        long start = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            String endpoint = httpRequest.getMethod() + " " + path;
            PerformanceMetrics.getInstance()
                    .recordRequest(endpoint, elapsedMs, httpResponse.getStatus());
        }
    }

    private boolean isExcluded(String path) {
        return path.endsWith("/metrics")
                || path.contains("/css/")
                || path.contains("/js/")
                || path.contains("/images/");
    }
}