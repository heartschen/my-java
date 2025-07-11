package com.holt.common.exception;

import com.holt.common.constant.ApiResponseCode;

/**
 * 登录异常
 */
public class LoginException extends BaseException {

    private static final long serialVersionUID = 979094253305695687L;

    public LoginException(String message) {
        super(ApiResponseCode.LOGIN_ERROR.getCode(), message);
    }

    public LoginException(String message, Throwable t) {
        super(message, t);
    }
}
