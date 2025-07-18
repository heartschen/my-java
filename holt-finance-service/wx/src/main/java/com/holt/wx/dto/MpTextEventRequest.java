package com.holt.wx.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 接收微信事件推送
 * 具体参数意思参考文档地址：
 * https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Receiving_event_pushes.html
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MpTextEventRequest extends MpBaseEventRequest {
    private static final long serialVersionUID = 4209945653336582616L;
    @JsonProperty(value = "Content")
    private String content;
}
