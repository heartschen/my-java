package com.holt.wx;

import com.holt.wx.dto.AccessTokenResult;
import com.holt.wx.dto.MpQrCodeCreateRequest;
import com.holt.wx.dto.MpQrCodeCreateResult;
import com.holt.wx.service.WXService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@SpringBootTest
public class WXTest {
    @Autowired
    private WXService wxService;

    @Test
    public void TestGetMpAccessToken() {
        AccessTokenResult accessTokenResult = getMpAccessToken();
        System.out.println(accessTokenResult.toString());
    }

    @Test
    public void TestCreateMpQrcodeCreate() {
        AccessTokenResult accessTokenResult = getMpAccessToken();
        MpQrCodeCreateRequest request = new MpQrCodeCreateRequest();
        request.setExpireSeconds(600);
        request.setActionName("QR_STR_SCENE");
        request.setActionInfo(request.new ActionInfo());
        request.getActionInfo().setScene(request.new scene());
        request.getActionInfo().getScene().setSceneStr("ScanReg_" + UUID.randomUUID().toString());
        MpQrCodeCreateResult result = wxService.createMpQrcodeCreate(accessTokenResult.getAccessToken(), request);
        System.out.println(result.toString());
    }

    private AccessTokenResult getMpAccessToken() {
        // 副业账号
        //AccessTokenResult accessTokenResult = wxService.getMpAccessToken("wxeb937d26b259854b", "8e96c570e234fa727f70b2149a2c1919");
        //微信测试号-token：504e3c5d01fd4e4b828acfe47c7137d9
        AccessTokenResult accessTokenResult = wxService.getMpAccessToken("wx08982655b0b37aba", "8241e2937d93fffc47c3e42e64ce84b7");

        return accessTokenResult;
    }
}
