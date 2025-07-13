package com.holt.finance.biz.service;


import com.holt.finance.biz.dto.form.PhoneRegisterForm;

public interface MemberRegService {

    /**
     * 注册 保存到数据库
     *
     * @param form
     * @return
     */
    long phoneReg(PhoneRegisterForm form);

}
