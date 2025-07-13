package com.holt.finance.biz.service.impl;


import com.holt.common.constant.CommonConstant;
import com.holt.finance.biz.domain.Member;
import com.holt.finance.biz.mapper.MemberMapper;
import com.holt.finance.biz.service.MemberService;
import com.holt.mybatis.help.MyBatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.holt.finance.biz.domain.MemberField.*;




@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    final MemberMapper memberMapper;

    /**
     * 注册
     *
     * @param tenantId 租户id
     * @return 会员id
     */
    @Override
    public long reg(long tenantId) {
        Member member = new Member();
        member.setTenantId(tenantId);
        member.setSysRoleIds("[" + CommonConstant.ROLE_MEMBER + "]");
        member.initDefault();
        memberMapper.insert(member);
        return member.getId();
    }

    /**
     * 获取会员信息
     *
     * @param id
     * @return
     */
    @Override
    public Member get(long id) {
        MyBatisWrapper<Member> myBatisWrapper = new MyBatisWrapper<>();
        myBatisWrapper.select(NickName, Id, Disable, Name, AvatarUrl, SysRoleIds,
                        TenantId, Email)
                .whereBuilder().andEq(setId(id));
        return memberMapper.topOne(myBatisWrapper);
    }

}
