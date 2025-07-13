package com.holt.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;


@Data
public class TokenResponse implements Serializable {
    private static final long serialVersionUID = 4786068357968425656L;
    /**
     * token 登录凭证
     */
    private String token;

    /**
     * 到期日期（时间戳-只读）
     */
    private Long expireDateTime;

    /**
     * 过期时间，多少时间后过期（单位通过timeUnit设置）
     */
    private Integer expire;

    /**
     * 过期时间单位
     */
    private TimeUnit timeUnit;
}
