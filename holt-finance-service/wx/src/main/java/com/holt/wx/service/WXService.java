package com.holt.wx.service;

import com.holt.wx.dto.AccessTokenResult;
import com.holt.wx.dto.MpQrCodeCreateRequest;
import com.holt.wx.dto.MpQrCodeCreateResult;

public interface WXService {
    /**
     * 获取微信公众号token
     * 参考接口文档：https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/access-token/auth.getAccessToken.html
     *
     * @param appid
     * @param secret
     * @return
     */
    AccessTokenResult getMpAccessToken(String appid, String secret);

    /**
     * 设置公众号token的redis缓存
     *
     * @param appid
     * @param secret
     */
//    void setMpAccessTokenCache(String appid, String secret);
//
//    /**
//     * 从缓存中获取公众号token
//     * @param appid
//     * @return
//     */
//    AccessTokenResult getMpAccessTokenByCache(String appid);

    /**
     * 生成临时公众号二维码
     * 文档地址：https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN
     * @param token
     * @param request
     * @return
     */
    MpQrCodeCreateResult createMpQrcodeCreate(String token, MpQrCodeCreateRequest request);
}
