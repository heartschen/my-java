package com.holt.finance.biz.service;

import com.holt.common.dto.TokenResponse;
import com.holt.finance.biz.dto.form.GetBase64CodeForm;
import com.holt.finance.biz.dto.form.GetSmsCodeForm;
import com.holt.finance.biz.dto.form.PhonePasswordLoginForm;
import com.holt.finance.biz.dto.form.PhoneSmsCodeLoginForm;

public interface MemberLoginService {
    /**
     * 获取客户端id
     * @return
     */
    String getClientId();

    /**
     * 获取图像验证码
     * @param form
     * @return
     */
    String getBase64Code(GetBase64CodeForm form);

    /**
     * 获取短信验证码
     * @param form
     */
    void sendSmsCode(GetSmsCodeForm form);

    /**
     * 校验短信验证码
     * @param phone
     * @param smsCode
     * @param smsCodeType
     * @return
     */
    boolean checkSmsCode(String phone, String smsCode, String smsCodeType);

    /**
     * 校验图形验证码
     * @param clientId
     * @return
     */
    boolean checkBase64Code(String clientId, String code);

    /**
     * 手机号密码登录
     * @param form
     * @return
     */
    TokenResponse phonePasswordLogin(PhonePasswordLoginForm form);

    /**
     * 手机号短信登录
     * @param form
     * @return
     */
    TokenResponse phoneSmsCodeLogin(PhoneSmsCodeLoginForm form);
}
