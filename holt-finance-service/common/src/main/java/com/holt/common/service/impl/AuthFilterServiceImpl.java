package com.holt.common.service.impl;

import com.holt.common.config.SecurityConfig;
import com.holt.common.constant.CommonConstant;
import com.holt.common.dto.BaseUserInfoDTO;
import com.holt.common.exception.BizException;
import com.holt.common.exception.LoginException;
import com.holt.common.service.AuthFilterService;
import com.holt.common.service.TokenService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ConditionalOnProperty(prefix = "sys", name = "enable-my-security", havingValue = "true")
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFilterServiceImpl<T> implements AuthFilterService<T> {
    final TokenService<T> tokenService;
    final AntPathMatcher antPathMatcher;
    final SecurityConfig securityConfig;
    final ObjectMapper jsonMapper;
    final HandlerExceptionResolver handlerExceptionResolver;
    final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (securityConfig == null || !securityConfig.getEnable()) {
                filterChain.doFilter(request, response);
                return;
            }

            T userInfo = null;
            if ("token".equals(securityConfig.getGetUserType())) {
                String token = request.getHeader("api-access-token");
                userInfo = tokenService.checkToken(token);
            }
            if ("gateway".equals(securityConfig.getGetUserType())) {
                String userInfoJson = request.getHeader("user");
                userInfo = jsonMapper.readValue(userInfoJson, new TypeReference<T>() {
                });
            }
            if (userInfo == null) {
                throw new LoginException("无法获取到用户信息");
            }
            BaseUserInfoDTO baseUserInfoDTO = (BaseUserInfoDTO) userInfo;
            //检查权限
            checkPermissions(baseUserInfoDTO.getSysRoleIds(), request.getServletPath());
            //T userInfo = userService.getRedisUser(tokenResponse.getToken());
            // 用户信息存储在线程中
            tokenService.setThreadLocalUser(userInfo);
            filterChain.doFilter(request, response);
            tokenService.removeThreadLocalUser();
        } catch (Exception ex) {
            handlerExceptionResolver.resolveException(request, response,
                    null, ex);
        }
    }

    /**
     * 不经过过滤器筛选
     *
     * @param request
     * @return
     * @throws ServletException
     */
    @Override
    public boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (securityConfig == null || !securityConfig.getEnable() || CollectionUtils.isEmpty(securityConfig.getIgnores())) {
            return false;
        }
        String path = request.getServletPath();
        boolean ignore = securityConfig.getIgnores().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
        if (log.isDebugEnabled()) {
            log.info("path: {} [ignore: {}]", path, ignore);
        }
        return ignore;
    }

    /**
     * 检查权限
     */
    public void checkPermissions(Set<Long> sysRoleIds, String path) {
        //如果是管理员则不检查权限，拥有所有权限
        if (sysRoleIds.contains(CommonConstant.ROLE_ADMIN)) {
            return;
        }
        Set<String> roleBindResourcePaths = listRoleResourcePathByCache(sysRoleIds);
        if (CollectionUtils.isEmpty(roleBindResourcePaths)) {
            throw new BizException("角色没有绑定任何资源");
        }
        for (String url : roleBindResourcePaths) {
            if (antPathMatcher.match(url, path)) {
                return;
            }
        }
        throw new BizException("非法访问");
    }

    /**
     * 从缓存中获取角色对应的菜单id
     *
     * @param roleIds
     * @return
     */
    private Set<String> listRoleResourcePathByCache(Set<Long> roleIds) {
        HashOperations<String, String, Set<String>> hashOps = redisTemplate.opsForHash();
        List<Set<String>> roleMenuIds = hashOps.multiGet(CommonConstant.ROLE_RESOURCE_PERMISSIONS, roleIds.stream().map(String::valueOf).collect(Collectors.toSet()));
        // 对结果进行处理，将 List<List<String>> 转为 List<String>
        return roleMenuIds.stream()
                .filter(p -> !CollectionUtils.isEmpty(p))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
