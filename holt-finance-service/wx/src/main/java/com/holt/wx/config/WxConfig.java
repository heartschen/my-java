package com.holt.wx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(
        prefix = "wx"
)
@Data
public class WxConfig {

    /**
     * 公众号配置
     */
    private WxDetailConfig mp;

    /**
     * 小程序配置
     */
    private WxDetailConfig miniApp;
}
