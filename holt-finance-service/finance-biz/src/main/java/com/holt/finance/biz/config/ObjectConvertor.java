package com.holt.finance.biz.config;

import com.holt.finance.biz.dto.vo.*;
import com.holt.wx.dto.MpQrCodeCreateResult;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ObjectConvertor {
    GenerateMpRegCodeVo toGenerateMpRegCodeResponse(MpQrCodeCreateResult source);
}