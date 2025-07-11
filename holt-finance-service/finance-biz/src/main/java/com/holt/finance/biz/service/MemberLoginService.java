package com.holt.finance.biz.service;

import com.holt.finance.biz.dto.form.GetBase64CodeForm;

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
}
