package com.holt.finance.biz.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.holt.common.constant.ApiResponseCode;
import com.holt.common.dto.TokenResponse;
import com.holt.common.exception.BizException;
import com.holt.common.exception.ParameterException;
import com.holt.common.service.TokenService;
import com.holt.common.util.DateUtil;
import com.holt.common.util.MyUtil;
import com.holt.finance.biz.constant.RedisKeyConstant;
import com.holt.finance.biz.domain.Member;
import com.holt.finance.biz.domain.MemberBindPhone;
import com.holt.finance.biz.dto.AdminDTO;
import com.holt.finance.biz.dto.form.*;
import com.holt.finance.biz.enums.SmsCodeTypeEnum;
import com.holt.finance.biz.service.MemberBindPhoneService;
import com.holt.finance.biz.service.MemberLoginService;
import com.holt.finance.biz.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberLoginServiceImpl implements MemberLoginService {
    final RedisTemplate<String,Object> redisTemplate;
    final MemberBindPhoneService memberBindPhoneService;
    final PasswordEncoder passwordEncoder;
    final MemberService memberService;
    final TokenService<AdminDTO> adminTokenService;

    final ObjectMapper jsonMapper;


    /**
     * 获取客户端id
     * @return
     */
    @Override
    public String getClientId() {
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * 获取图像验证码
     * @param form
     * @return
     */
    @Override
    public String getBase64Code(GetBase64CodeForm form) {
      LineCaptcha lineCaptcha =  CaptchaUtil.createLineCaptcha(300,192,5,1000);
      //将图像验证码写到redis缓存中
      String code = lineCaptcha.getCode();
      log.info("客户端id{},图形验证码：{}", form.getClientId(), code);
        redisTemplate.opsForValue().set(RedisKeyConstant.GRAPHIC_VERIFICATION_CODE + form.getClientId(),code,15, TimeUnit.MINUTES);

      return lineCaptcha.getImageBase64();
    }

    /**
     * 获取短信验证码
     *
     * @param form
     */
    @Override
    public void sendSmsCode(GetSmsCodeForm form)  {
        //校验图形验证码
        checkBase64Code(form.getClientId(), form.getCode());
        //redis中SMS存储的key
        String key = RedisKeyConstant.SMS_CODE + form.getSmsCodeType() + form.getPhone();
        //从redis中获取短信验证码发送信息
        SmsCodeResult smsCodeResult = (SmsCodeResult) redisTemplate.opsForValue().get(key);
        //如果获取到的短信验证码发送信息不为空，则判断获取时间与当前时间的差值是否小于60秒
        if (smsCodeResult != null) {
            Duration duration = DateUtil.getDuration(smsCodeResult.getGetTime(), DateUtil.getSystemTime());

            if (duration.getSeconds() < 60) {
                throw new BizException("验证码获取太频繁，请稍后重试");
            }
        }
        MemberBindPhone memberBindPhone = memberBindPhoneService.getMemberByPhone(form.getPhone());
        if (form.getSmsCodeType().equals(SmsCodeTypeEnum.REG.getCode()) && memberBindPhone != null) {
            throw new ParameterException("phone", "该手机号已注册！");
        }
        if (form.getSmsCodeType().equals(SmsCodeTypeEnum.LOGIN.getCode()) && memberBindPhone == null) {
            throw new ParameterException("phone", "该手机号未注册！");
        }
        int smsCode = MyUtil.getRandom(6);
        smsCodeResult = new SmsCodeResult();
        smsCodeResult.setCode(String.valueOf(smsCode));
        smsCodeResult.setGetTime(DateUtil.getSystemTime());
        redisTemplate.opsForValue().set(key, smsCodeResult, 15, TimeUnit.MINUTES);
        log.info("客户端id{},手机号：{},短信验证码：{}", form.getClientId(), form.getPhone(), smsCode);
        //TODO: 调用第三方短信服务发送短信验证码
//        smsService.sendSmsCode(form.getPhone(), smsCodeResult.getCode(), form.getSmsCodeType());

    }

    /**
     * 校验短信验证码
     *
     * @param phone
     * @param smsCode
     * @param smsCodeType
     * @return
     */
    @Override
    public boolean checkSmsCode(String phone, String smsCode, String smsCodeType) {
        SmsCodeResult cacheSmsCode = (SmsCodeResult) redisTemplate.opsForValue().get(RedisKeyConstant.SMS_CODE + smsCodeType + phone);
        redisTemplate.delete(RedisKeyConstant.SMS_CODE + smsCodeType + phone);
        if (cacheSmsCode == null || !smsCode.equals(cacheSmsCode.getCode())) {
            throw new ParameterException("smsCode", "短信证码错误，请重新获取验证码！");
        }
        return true;
    }

    /**
     * 校验图形验证码
     *
     * @param clientId
     * @return
     */
    @Override
    public boolean checkBase64Code(String clientId, String code) {
        //生成图片，获取base64编码字符串
        String cacheCode = (String) redisTemplate.opsForValue().get(RedisKeyConstant.GRAPHIC_VERIFICATION_CODE + clientId);
        redisTemplate.delete(RedisKeyConstant.GRAPHIC_VERIFICATION_CODE + clientId);
        if (!code.equalsIgnoreCase(cacheCode)) {
            throw new ParameterException("code", "图形验证码错误！");
        }
        return true;
    }

    /**
     * 手机号密码登录
     * @param form
     * @return
     */
    @Override
    public TokenResponse phonePasswordLogin(PhonePasswordLoginForm form) {
       //校验验证码是否正确
        checkBase64Code(form.getClientId(), form.getCode());
        //查询手机是否注册
        MemberBindPhone memberBindPhone = memberBindPhoneService.getMemberByPhone(form.getPhone());
        if(memberBindPhone == null || Strings.isBlank(memberBindPhone.getPassword())){
            throw new BizException(ApiResponseCode.ACCOUNT_PASSWORD_ERROR.getCode(),
                    ApiResponseCode.ACCOUNT_PASSWORD_ERROR.getMessage());
        }
        //如果存在，校验密码是否正确
        if (!passwordEncoder.matches(form.getPassword(), memberBindPhone.getPassword())) {
            throw new BizException(ApiResponseCode.ACCOUNT_PASSWORD_ERROR.getCode(),
                    ApiResponseCode.ACCOUNT_PASSWORD_ERROR.getMessage());
        }

        //登录成功，返回token
        Member member = memberService.get(memberBindPhone.getMemberId());
        return loginSuccess(memberBindPhone.getMemberId(), member.getTenantId(), member.getSysRoleIds());

    }

    @Override
    public TokenResponse phoneSmsCodeLogin(PhoneSmsCodeLoginForm form) {
        //校验短信验证码是否正确
        checkSmsCode(form.getPhone(), form.getSmsCode(), SmsCodeTypeEnum.LOGIN.getCode());
        //根据手机获取手机用户信息
        MemberBindPhone memberBindPhone = memberBindPhoneService.getMemberByPhone(form.getPhone());
        //手机号未注册
        if (memberBindPhone == null) {
            throw new ParameterException("phone",
                    "该手机号未注册");
        }
        //根据手机查出的用户会员id，获取用户详细信息
        Member member = memberService.get(memberBindPhone.getMemberId());
        return loginSuccess(memberBindPhone.getMemberId(), member.getTenantId(), member.getSysRoleIds());

    }

    private TokenResponse loginSuccess(long memberId, long tenantId, String sysRoleIds) {
        try {
            AdminDTO adminDTO = new AdminDTO();
            adminDTO.setId(memberId);
            adminDTO.setTenantId(tenantId);
            adminDTO.setSysRoleIds(jsonMapper.readValue(sysRoleIds, new TypeReference<Set<Long>>() {
            }));
            adminTokenService.setToken(adminDTO);
//        redisTemplate.opsForValue().set(RedisKeyConstant.CLIENT_TOKEN_KEY + memberId, adminDTO.getToken(), 10, TimeUnit.MINUTES);
            return adminDTO.getToken();
        } catch (Exception ex) {
            throw new BizException("登录失败", ex);
        }
    }
}



