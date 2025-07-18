package com.holt.wx.config;

import lombok.Data;

@Data
public class WxDetailConfig {

    /**
     * appid
     */
    private String appId;

    /**
     * 秘钥
     */
    private String secret;

    /**
     * 二维码过期时间
     * 默认15分钟
     */
    private Integer codeExpire = 900;

    /**
     * token
     */
    private String token;

    /**
     * 消息加密密钥
     */
    private String encodingAESKey;
}
