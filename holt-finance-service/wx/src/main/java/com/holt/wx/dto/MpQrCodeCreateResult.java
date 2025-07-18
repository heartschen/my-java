package com.holt.wx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MpQrCodeCreateResult implements Serializable {
    /**
     * 获取的二维码ticket，凭借此 ticket 可以在有效时间内换取二维码。
     */
    private String ticket;

    /**
     * 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天）。
     */
    @JsonProperty(value = "expire_seconds")
    private Integer expireSeconds;

    /**
     * 二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片
     */
    private String url;

    /**
     * 错误码
     */
    @JsonProperty(value = "errcode")
    private String errCode;
    /**
     * 错误信息
     */
    @JsonProperty(value = "errmsg")
    private String errMsg;

    /**
     * 二维码地址
     */
    private String qrCodeUrl;
}