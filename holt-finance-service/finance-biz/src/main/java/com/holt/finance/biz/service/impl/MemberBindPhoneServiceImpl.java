package com.holt.finance.biz.service.impl;


import com.holt.finance.biz.domain.MemberBindPhone;
import com.holt.finance.biz.mapper.MemberBindPhoneMapper;
import com.holt.finance.biz.service.MemberBindPhoneService;
import com.holt.mybatis.help.MyBatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import static com.holt.finance.biz.domain.MemberBindPhoneField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberBindPhoneServiceImpl implements MemberBindPhoneService {
    final MemberBindPhoneMapper memberBindPhoneMapper;
    final PasswordEncoder passwordEncoder;

    /**
     * 根据手机号获取用户信息
     *
     * @param phone
     * @return
     */
    @Override
    public MemberBindPhone getMemberByPhone(String phone) {
        MyBatisWrapper<MemberBindPhone> myBatisWrapper = new MyBatisWrapper<>();
        myBatisWrapper.select(MemberId, Phone, Password)
                .whereBuilder().andEq(setPhone(phone))
                .andEq(setDisable(false));
        // select member_id,phone,password from member_bind_phone where phone = ?
        return memberBindPhoneMapper.topOne(myBatisWrapper);
    }


    /**
     * 手机号注册
     *
     * @param phone
     * @param memberId
     * @param password
     * @return
     */
    @Override
    public boolean reg(String phone, long memberId, String password) {
        MemberBindPhone memberBindPhone = new MemberBindPhone();
        memberBindPhone.setMemberId(memberId);
        memberBindPhone.setPhone(phone);
        memberBindPhone.setPassword(passwordEncoder.encode(password));
        memberBindPhone.initDefault();
        return memberBindPhoneMapper.insert(memberBindPhone) > 0;
    }

}
