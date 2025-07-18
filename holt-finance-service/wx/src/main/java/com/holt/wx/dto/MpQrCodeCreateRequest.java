package com.holt.wx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MpQrCodeCreateRequest {
    /**
     * 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为60秒。
     */
    @JsonProperty(value = "expire_seconds")
    private Integer expireSeconds;

    /**
     * 二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值
     */
    @JsonProperty(value = "action_name")
    private String actionName;

    /**
     * 二维码详细信息
     */
    @JsonProperty(value = "action_info")
    private ActionInfo actionInfo;


    @Data
    public class ActionInfo {
        private scene scene;
    }

    @Data
    public class scene {
        /**
         * 场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
         */
        @JsonProperty(value = "scene_id")
        private Integer sceneId;

        /**
         * 场景值ID（字符串形式的ID），字符串类型，长度限制为1到64
         */
        @JsonProperty(value = "scene_str")
        private String sceneStr;
    }
}