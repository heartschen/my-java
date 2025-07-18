package com.holt.finance.admin.api.controller;

import com.holt.common.dto.ApiResponse;
import com.holt.common.dto.TokenResponse;
import com.holt.finance.biz.dto.form.*;
import com.holt.finance.biz.service.MemberLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户登录模块")
@RestController
@RequestMapping(value = "/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    final MemberLoginService memberLoginService;

    @ApiOperation(value = "获取客户端Id")
    @GetMapping(value = "/getClientId")
    public ApiResponse<String> getClientId() {
       return ApiResponse.success(memberLoginService.getClientId());
    }

    @ApiOperation(value = "获取图像验证码")
    @GetMapping(value = "/getBase64Code")
    public ApiResponse<String> getBase64Code(@Validated @ModelAttribute GetBase64CodeForm form){
        return ApiResponse.success(memberLoginService.getBase64Code(form));
    }

    @ApiOperation(value = "获取短信验证码")
    @GetMapping(value = "/sendSmsCode")
    public ApiResponse<Void> sendSmsCode(@Validated @ModelAttribute GetSmsCodeForm form) {
        memberLoginService.sendSmsCode(form);
        return ApiResponse.success();
    }

    @ApiOperation(value = "手机号登录")
    @PostMapping(value = "/phone/login")
    public ApiResponse<TokenResponse> phonePasswordLogin(@Validated @RequestBody PhonePasswordLoginForm form) {
        return ApiResponse.success( memberLoginService.phonePasswordLogin(form));
    }

    @ApiOperation(value = "手机短信登录")
    @PostMapping(value = "/phoneSmsCodeLogin")
    public ApiResponse<TokenResponse> phoneSmsCodeLogin(@Validated @RequestBody PhoneSmsCodeLoginForm request) {
        return ApiResponse.success(memberLoginService.phoneSmsCodeLogin(request));
    }

    @ApiOperation(value = "获取客户端token")
    @GetMapping(value = "/getClientToken")
    public ApiResponse<TokenResponse> getClientToken(@Validated @ModelAttribute GetClientTokenForm request) {
        TokenResponse result = memberLoginService.getClientToken(request.getClientId());
        return ApiResponse.success(result);
    }
}
