package com.holt.finance.admin.api.controller;

import com.holt.wx.aes.AesException;
import com.holt.wx.dto.MpCommonRequest;
import com.holt.wx.service.WxMpEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Api(tags = "微信模块")
@RestController
@RequestMapping(value = "/wxEvent")
@RequiredArgsConstructor
@Slf4j
public class WxEventController {
    final WxMpEventService wxMpEventService;

    /**
     * 接收微信事件推送
     * 参考文档：
     * https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Receiving_event_pushes.html
     * *
     *
     * @param mpCommonRequest
     * @return
     * @throws HttpRequestMethodNotSupportedException
     */
    @ApiOperation(value = "接收微信公众号推送事件")
    @RequestMapping(value = "/receiveMpEvent",
            method = {RequestMethod.GET, RequestMethod.POST})
    public String receiveMpEvent(@Validated @ModelAttribute MpCommonRequest mpCommonRequest, HttpServletRequest httpServletRequest) throws IOException, AesException {
        return wxMpEventService.receiveMpEvent(mpCommonRequest, httpServletRequest);
    }
}
