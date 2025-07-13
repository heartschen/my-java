package com.holt.common.service;

import java.util.Map;

public interface SmsCommonService {
    /**
     * 发送短信
     *
     * @param signName      签名
     * @param templateCode  模板编号
     * @param phoneNumbers  手机号
     * @param templateParam 模板参数
     * @throws Exception
     */
    void sendSms(String signName, String templateCode, String phoneNumbers, Map<String, String> templateParam);
}