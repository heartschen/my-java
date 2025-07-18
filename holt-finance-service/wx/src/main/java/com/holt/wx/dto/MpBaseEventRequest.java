package com.holt.wx.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 接收微信事件推送
 * 具体参数意思参考文档地址：
 * https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Receiving_event_pushes.html
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MpBaseEventRequest implements Serializable {
    private static final long serialVersionUID = 9194059022155191588L;
    /**
     * 开发者微信号
     */
    @JsonProperty(value = "ToUserName")
    private String toUserName;

    /**
     * 发送方帐号（一个OpenID）
     */
    @JsonProperty(value = "FromUserName")
    private String fromUserName;

    /**
     * 消息创建时间 （整型）
     */
    @JsonProperty(value = "CreateTime")
    private Integer createTime;

    /**
     * 消息类型，event
     */
    @JsonProperty(value = "MsgType")
    private String msgType;

//    /**
//     * 事件类型
//     * subscribe(订阅/)
//     * unsubscribe(取消订阅)
//     * SCAN(用户已关注扫描)
//     * LOCATION 上报地理位置
//     * CLICK 菜单点击事件
//     */
//    @JsonProperty(value = "Event")
//    private String event;

//    /**
//     * 事件 KEY 值，是一个32位无符号整数，即创建二维码时的二维码scene_id
//     */
//    @JsonProperty(value = "EventKey")
//    private String eventKey;
//
//    /**
//     * 二维码的ticket，可用来换取二维码图片
//     */
//    @JsonProperty(value = "Ticket")
//    private String ticket;
//
//    /**
//     * 地理位置纬度
//     */
//    @JsonProperty(value = "Latitude")
//    private Double latitude;
//
//    /**
//     * 地理位置经度
//     */
//    @JsonProperty(value = "Longitude")
//    private Double longitude;
//
//    /**
//     * 地理位置精度
//     */
//    @JsonProperty(value = "Precision")
//    private Double precision;
}
