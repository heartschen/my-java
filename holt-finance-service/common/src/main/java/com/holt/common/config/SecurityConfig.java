package com.holt.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(
        prefix = "security"
)
@Data
public class SecurityConfig {
    /**
     * 是否开启安全系统
     */
    private Boolean enable = false;
    /**
     * 获取用户信息方式
     * token：从token中获取
     * gateway：从网关传递的请求头user中获取
     */
    private String getUserType = "token";

    /**
     * token 过期时间（秒）
     * 默认1个小时
     */
    private Integer expire = 3600;
    private List<String> ignores;
}
