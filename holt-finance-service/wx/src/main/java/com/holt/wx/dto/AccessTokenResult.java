package com.holt.wx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 获取微信token返回接口实体信息
 * 参考文档：https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/access-token/auth.getAccessToken.html
 */
@Data
public class AccessTokenResult {
    /**
     * 获取到的凭证
     */
    @JsonProperty(value = "access_token", required = true)
    private String accessToken;
    /**
     * 凭证有效时间，单位：秒。目前是7200秒之内的值。
     */
    @JsonProperty(value = "expires_in", required = true)
    private String expiresIn;
    /**
     * 错误码
     */
    @JsonProperty(value = "errcode", required = true)
    private String errCode;
    /**
     * 错误信息
     */
    @JsonProperty(value = "errmsg", required = true)
    private String errMsg;
}
