package com.holt.finance.biz.service.impl;

import com.holt.common.dto.TokenResponse;
import com.holt.common.exception.BizException;
import com.holt.common.exception.ParameterException;
import com.holt.common.service.TokenService;
import com.holt.finance.biz.config.ObjectConvertor;
import com.holt.finance.biz.constant.RedisKeyConstant;
import com.holt.finance.biz.domain.MemberBindPhone;
import com.holt.finance.biz.domain.MemberBindWxOpenId;
import com.holt.finance.biz.dto.AdminDTO;
import com.holt.finance.biz.dto.form.PhoneRegisterForm;
import com.holt.finance.biz.dto.vo.GenerateMpRegCodeVo;
import com.holt.finance.biz.enums.SmsCodeTypeEnum;
import com.holt.finance.biz.service.*;
import com.holt.wx.config.WxConfig;
import com.holt.wx.dto.AccessTokenResult;
import com.holt.wx.dto.MpQrCodeCreateRequest;
import com.holt.wx.dto.MpQrCodeCreateResult;
import com.holt.wx.dto.MpSubscribeEventRequest;
import com.holt.wx.service.WXService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


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
    final WxConfig wxConfig;
    final WXService wxService;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> adminTokenService;

    final MemberBindWxOpenIdService memberBindWxOpenIdService;



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

    /**
     * 生成微信公众号二维码 用于关注注册
     *
     * @param clientId
     * @return
     */
    @Override
    public GenerateMpRegCodeVo generateMpRegCode(String clientId) {
        //获取微信公众号的AccessToken
        AccessTokenResult accessTokenResult = wxService.getMpAccessToken(wxConfig.getMp().getAppId(), wxConfig.getMp().getSecret());
        //获取二维码参数
        MpQrCodeCreateRequest request = new MpQrCodeCreateRequest();
        request.setExpireSeconds(wxConfig.getMp().getCodeExpire());
        request.setActionName("QR_STR_SCENE");
        request.setActionInfo(request.new ActionInfo());
        request.getActionInfo().setScene(request.new scene());
        request.getActionInfo().getScene().setSceneStr("ScanReg_" + wxConfig.getMp().getAppId() + "_" + clientId);
        //掉公用方法生成二维码
        MpQrCodeCreateResult result = wxService.createMpQrcodeCreate(accessTokenResult.getAccessToken(), request);

        return objectConvertor.toGenerateMpRegCodeResponse(result);
    }

    /**
     * 监听微信公众号推送过来的订阅事件
     * @param mpSubscribeEventRequest
     */
    @EventListener
    @Override
    public void handleMpSubscribeEventRequest(MpSubscribeEventRequest mpSubscribeEventRequest) {
        log.info("接收到消息：MpSubscribeEventRequest：{}", mpSubscribeEventRequest.toString());
        log.info("0:{}", mpSubscribeEventRequest.getEvent());

        if ("subscribe".equals(mpSubscribeEventRequest.getEvent())
                && Strings.isNotBlank(mpSubscribeEventRequest.getEventKey())) {
            String[] keys = mpSubscribeEventRequest.getEventKey().split("_");
            if ("qrscene".equals(keys[0]) && "ScanReg".equals(keys[1])) {
                log.info("AppId：{}，ClientId：{}", keys[2], keys[3]);
                registerByMpOpenId(keys[2], keys[3], mpSubscribeEventRequest.getToUserName());
                return;
            }
        }

        if ("SCAN".equals(mpSubscribeEventRequest.getEvent()) &&
                Strings.isNotBlank(mpSubscribeEventRequest.getEventKey())) {
            String[] keys = mpSubscribeEventRequest.getEventKey().split("_");
            if ("ScanReg".equals(keys[0])) {
                log.info("AppId：{}，ClientId：{}", keys[1], keys[2]);
                registerByMpOpenId(keys[1], keys[2], mpSubscribeEventRequest.getToUserName());
                return;
            }
        }
    }

    public TokenResponse registerByMpOpenId(String appId, String clientId, String openId) {
        long memberId = scReg(appId, openId);
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setId(memberId);
        adminTokenService.setToken(adminDTO);
        redisTemplate.opsForValue().set(RedisKeyConstant.CLIENT_TOKEN_KEY + clientId, adminDTO.getToken(), 10, TimeUnit.MINUTES);
        return adminDTO.getToken();
    }

    /**
     * 扫描注册
     *
     * @param appId
     * @param openId
     * @return
     */
    @Override
    public long scReg(String appId, String openId) {
        MemberBindWxOpenId memberBindWxOpenId = memberBindWxOpenIdService.get(appId, openId);
        if (Objects.nonNull(memberBindWxOpenId)) {
            return memberBindWxOpenId.getMemberId();
        }

        //将游客数据入口（保证数据一致性）
        Long memberId = transactionTemplate.execute(transactionStatus -> {
            //创建租户id
            long tenantId = tenantService.add();
            long id = memberService.reg(tenantId);
            memberBindWxOpenIdService.reg(appId, openId, id);
            return id;
        });
        if (memberId == null) {
            throw new BizException("注册失败");
        }
        return memberId;
    }




}
