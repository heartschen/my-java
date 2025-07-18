package com.holt.wx.service.impl;

import com.holt.wx.constant.WXApiConstant;
import com.holt.wx.dto.AccessTokenResult;
import com.holt.wx.dto.MpQrCodeCreateRequest;
import com.holt.wx.dto.MpQrCodeCreateResult;
import com.holt.wx.service.WXService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class WXServiceImpl implements WXService {
    final WebClient webClient;
//    final RedisTemplate<String, Object> redisTemplate;
//    final String MP_TOKEN_REDIS_CACHE_KEY = "MP_TOKEN_REDIS_CACHE_KEY:";
    //接口重试次数
    int retry = 3;

    /**
     * 获取微信公众号token
     * 参考接口文档：https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/access-token/auth.getAccessToken.html
     *
     * @param appid
     * @param secret
     * @return
     */
    @Override
    public AccessTokenResult getMpAccessToken(String appid, String secret) {
        String url = String.format(WXApiConstant.ACCESS_TOKEN_API, appid, secret);
        return webClient.get().uri(url).retrieve().bodyToMono(AccessTokenResult.class)
                .retry(retry)
                .block();
    }

    /**
     * 设置公众号token的redis缓存
     *
     * @param appid
     * @param secret
     */
//    @Override
//    public void setMpAccessTokenCache(String appid, String secret) {
//        AccessTokenResult accessTokenResult = getMpAccessToken(appid, secret);
//        redisTemplate.opsForValue().set(MP_TOKEN_REDIS_CACHE_KEY + appid, accessTokenResult);
//    }
//
//    /**
//     * 从缓存中获取公众号token
//     * @param appid
//     * @return
//     */
//    @Override
//    public AccessTokenResult getMpAccessTokenByCache(String appid) {
//        return (AccessTokenResult) redisTemplate.opsForValue().get(MP_TOKEN_REDIS_CACHE_KEY + appid);
//    }



    /**
     * 生成临时公众号二维码
     * 文档地址：https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN
     *
     * @param token
     * @param request
     * @return
     */
    @Override
    public MpQrCodeCreateResult createMpQrcodeCreate(String token, MpQrCodeCreateRequest request) {
        String url = String.format(WXApiConstant.MP_QRCODE_CREATE, token);
        MpQrCodeCreateResult result = webClient.post().uri(url).contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve().bodyToMono(MpQrCodeCreateResult.class).retry(retry)
                .block();
        if (result == null || Strings.isBlank(result.getTicket())) {
            return result;
        }
        result.setQrCodeUrl("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + result.getTicket());
        return result;
    }


}
