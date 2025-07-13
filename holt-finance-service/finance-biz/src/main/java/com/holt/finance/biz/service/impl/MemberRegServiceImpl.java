package com.holt.finance.biz.service.impl;

import com.holt.common.exception.BizException;
import com.holt.common.exception.ParameterException;
import com.holt.finance.biz.constant.RedisKeyConstant;
import com.holt.finance.biz.domain.MemberBindPhone;
import com.holt.finance.biz.dto.form.PhoneRegisterForm;
import com.holt.finance.biz.enums.SmsCodeTypeEnum;
import com.holt.finance.biz.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;


@Service
@Slf4j
@RequiredArgsConstructor
public class MemberRegServiceImpl implements MemberRegService {
    final TenantService tenantService;
    final MemberService memberService;
    final MemberLoginService memberLoginService;
    final RedissonClient redissonClient;
    final MemberBindPhoneService memberBindPhoneService;
    final TransactionTemplate transactionTemplate;
    final RedisTemplate<String, Object> redisTemplate;

    /**
     * 注册 保存到数据库
     *
     * @param request
     * @return
     */
    @Override
    public long phoneReg(PhoneRegisterForm request) {
        //校验两次密码是否一致
        if (!Objects.equals(request.getPassword(), request.getConfirmPassword())) {
            throw new ParameterException("两次输入的密码不一致");
        }
        // 校验redis中的缓存的短信验证码，和传入的是否一致
        memberLoginService.checkSmsCode(request.getPhone(), request.getSmsCode(), SmsCodeTypeEnum.REG.getCode());

        //获取分布式锁，防止并发注册同一个手机号
        RLock rLock = redissonClient.getLock(RedisKeyConstant.PHONE_CHANGE + request.getPhone());
        try {
            rLock.lock();
            MemberBindPhone memberBindPhone = memberBindPhoneService.getMemberByPhone(request.getPhone());
            if (memberBindPhone != null) {
                log.warn("手机号：{}已注册", request.getPhone());
                throw new BizException("手机号已注册");
            }
            //使用事物，尽量不要用注解事物
            //将游客数据入口（保证数据一致性）
            Long memberId = transactionTemplate.execute(transactionStatus -> {
                //创建租户，返回租户ID
                long tenantId = tenantService.add();
                //注册会员,返回会员ID
                long id = memberService.reg(tenantId);
                if (id <= 0) {
                    throw new BizException("注册异常");
                }
                memberBindPhoneService.reg(request.getPhone(), id, request.getPassword());
                return id;
            });
            if (memberId == null) {
                throw new BizException("注册失败");
            }
            return memberId;
        } catch (Exception ex) {
            throw new BizException("注册失败", ex);
        } finally {
            rLock.unlock();
        }
    }

}
