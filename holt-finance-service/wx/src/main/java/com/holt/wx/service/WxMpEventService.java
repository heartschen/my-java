package com.holt.wx.service;

import com.holt.wx.aes.AesException;
import com.holt.wx.dto.MpCommonRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface WxMpEventService {
    /**
     * 接收公众号推送的事件
     *
     * @param mpCommonRequest
     * @param httpServletRequest
     * @return
     * @throws IOException
     */
    String receiveMpEvent(MpCommonRequest mpCommonRequest, HttpServletRequest httpServletRequest) throws IOException, AesException;

    /**
     * 用SHA1算法生成安全签名
     *
     * @param signature 签名
     * @param token     票据
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @param encrypt   密文
     * @return 安全签名
     * @throws AesException
     */
    void checkSignature(String signature, String token, String timestamp, String nonce, String encrypt) throws AesException;
}
