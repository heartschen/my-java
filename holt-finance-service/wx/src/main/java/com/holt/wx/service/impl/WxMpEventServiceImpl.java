package com.holt.wx.service.impl;

import com.holt.common.exception.ParameterException;
import com.holt.wx.aes.AesException;
import com.holt.wx.config.WxConfig;
import com.holt.wx.dto.MpBaseEventRequest;
import com.holt.wx.dto.MpCommonRequest;
import com.holt.wx.dto.MpSubscribeEventRequest;
import com.holt.wx.dto.MpTextEventRequest;
import com.holt.wx.service.WxMpEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class WxMpEventServiceImpl implements WxMpEventService {
    final ApplicationContext applicationContext;
    final WxConfig wxConfig;

    /**
     * 接收公众号推送的事件
     *
     * @param mpCommonRequest
     * @param httpServletRequest
     * @return
     * @throws IOException
     */
    @Override
    public String receiveMpEvent(MpCommonRequest mpCommonRequest, HttpServletRequest httpServletRequest) throws IOException, AesException {
        //微信解密算法
        checkSignature(mpCommonRequest.getSignature(),
                wxConfig.getMp().getToken(),
                mpCommonRequest.getTimestamp(),
                mpCommonRequest.getNonce(),
                null);
        //判断content-type是否为空
        if (Strings.isBlank(httpServletRequest.getHeader("content-type"))) {
            log.info("content-type is null");
            return mpCommonRequest.getEchostr();
        }
        log.info("content-type:" + httpServletRequest.getHeader("content-type"));
        log.info(mpCommonRequest.toString());

        XmlMapper xmlMapper = new XmlMapper();
        Object object = xmlMapper.readValue(httpServletRequest.getInputStream(), Object.class);
        ObjectMapper mapper = new ObjectMapper();
        log.info(object.toString());
        // 转换成一个基本的对象
        MpBaseEventRequest mpBaseEventRequest = mapper.convertValue(object, MpBaseEventRequest.class);
        if ("text".equals(mpBaseEventRequest.getMsgType())) {
            MpTextEventRequest mpTextEventRequest = mapper.convertValue(object, MpTextEventRequest.class);
            log.info(mpTextEventRequest.toString());
            log.info("推送消息：MpTextEventRequest...");
            applicationContext.publishEvent(mpTextEventRequest);
        }
        if ("event".equals(mpBaseEventRequest.getMsgType())) {
            MpSubscribeEventRequest mpSubscribeEventRequest = mapper.convertValue(object, MpSubscribeEventRequest.class);
            log.info(mpSubscribeEventRequest.toString());
            log.info("推送消息：MpSubscribeEventRequest...");
            applicationContext.publishEvent(mpSubscribeEventRequest);
        }
        log.info("微信公众号推送事件处理完成");
        return mpCommonRequest.getEchostr();
    }

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
    @Override
    public void checkSignature(String signature, String token, String timestamp, String nonce, String encrypt) throws AesException {
        try {
            if (Strings.isBlank(signature) || Strings.isBlank(token)
                    || Strings.isBlank(timestamp) || Strings.isBlank(nonce)) {
                throw new ParameterException("签名参数非法");
            }
            String[] array = null;
            if (Strings.isBlank(encrypt)) {
                array = new String[]{token, timestamp, nonce};
            } else {
                array = new String[]{token, timestamp, nonce, encrypt};
            }

            StringBuffer sb = new StringBuffer();
            // 字符串排序
            Arrays.sort(array);
            for (int i = 0; i < array.length; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            // SHA1签名生成
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();

            StringBuffer hexstr = new StringBuffer();
            String shaHex = "";
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            log.info("wx_signature:{},my_signature：{}", signature, hexstr.toString());
            if (!Objects.equals(signature, hexstr.toString())) {
                throw new AesException(AesException.ValidateSignatureError);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.ComputeSignatureError);
        }
    }
}
