package com.holt.finance.biz.service;

import com.holt.finance.biz.domain.MemberBindPhone;
import com.holt.finance.biz.dto.form.UpdatePasswordForm;
import com.holt.finance.biz.dto.form.UpdatePhoneForm;

public interface MemberBindPhoneService {
    /**
     * 根据手机号获取用户信息
     *
     * @param phone
     * @return
     */
    MemberBindPhone getMemberByPhone(String phone);

    /**
     * 手机号注册
     *
     * @param phone
     * @param memberId
     * @param password
     * @return
     */
    boolean reg(String phone, long memberId, String password);

}
