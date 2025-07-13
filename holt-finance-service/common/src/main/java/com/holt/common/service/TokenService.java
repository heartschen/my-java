package com.holt.common.service;

import com.holt.common.dto.TokenResponse;

public interface TokenService<T> {
    /**
     * 是否刷新token（用于操作接口时，是否给token续命）
     *
     * @param tokenResponse
     * @return
     */
    boolean isRefreshToken(TokenResponse tokenResponse);

    /**
     * 1、检验token
     * 2、根据规则判断是否要刷新token
     * @param token
     * @return
     */
    T checkToken(String token);

    /**
     * 设置token
     *
     * @param userInfo
     */
    T setToken(T userInfo);

    /**
     * 获取用户信息
     *
     * @return
     */
    T getThreadLocalUser();

    /**
     * 设置用户信息
     *
     * @param userInfo
     */
    void setThreadLocalUser(T userInfo);

    /**
     * 删除本地用户
     */
    void removeThreadLocalUser();

    /**
     * 获取用户信息
     *
     * @param token
     * @return
     */
    T getRedisUser(String token);

    /**
     * 获取用户id
     *
     * @return
     */
    Long getThreadLocalUserId();

    /**
     * 获取租户id
     *
     * @return
     */
    Long getThreadLocalTenantId();

    /**
     * 清除token
     *
     * @return
     */
    void clearToken();
}
