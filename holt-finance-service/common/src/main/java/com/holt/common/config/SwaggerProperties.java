package com.holt.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Swagger 属性
 *
 */
@Component
@ConfigurationProperties(prefix = "swagger")
@Data
public class SwaggerProperties {

    /**
     * 是否开启Swagger，UAT环境以及生产环境一般关闭。
     */
    private Boolean enable = false;

    /**
     * 项目应用名
     */
    private String name;

    /**
     * 项目版本信息
     */
    private String version;

    /**
     * 项目描述信息
     */
    private String description;

    /**
     * 接口前缀
     */
    private String pathMapping;
}
