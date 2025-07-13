package com.holt.common.dto;

import lombok.Data;

import java.util.Set;

@Data
public class BaseUserInfoDTO {
    /**
     * 用户id
     */
    private Long id;

    /**
     * 角色id
     */
    private Set<Long> sysRoleIds;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * token
     */
    private TokenResponse token;
}
