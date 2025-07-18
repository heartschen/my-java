package com.holt.common.service;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthFilterService<T> {
    void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException;

    /**
     * 不经过过滤器筛选
     *
     * @param request
     * @return
     * @throws ServletException
     */
    boolean shouldNotFilter(HttpServletRequest request) throws ServletException;
}
