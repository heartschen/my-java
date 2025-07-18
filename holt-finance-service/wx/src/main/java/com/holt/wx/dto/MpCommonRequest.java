package com.holt.wx.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MpCommonRequest {
    /**
     * 微信加密签名，signature结合了开发者填写的 token 参数和请求中的 timestamp 参数、nonce参数。
     */
    @NotBlank
    private String signature;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 随机数
     */
    private String nonce;

    /**
     * 随机字符串
     */
    private String echostr;
}
