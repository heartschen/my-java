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
public class MpSubscribeEventRequest extends MpBaseEventRequest {
    /**
     * 事件类型
     * subscribe(订阅/)
     * unsubscribe(取消订阅)
     * SCAN(用户已关注扫描)
     * LOCATION 上报地理位置
     * CLICK 菜单点击事件
     */
    @JsonProperty(value = "Event")
    private String event;

    /**
     * 事件 KEY 值，qrscene_为前缀，后面为二维码的参数值
     */
    @JsonProperty(value = "EventKey")
    private String eventKey;

    /**
     * 二维码的ticket，可用来换取二维码图片
     */
    @JsonProperty(value = "Ticket")
    private String ticket;
}
