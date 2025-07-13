package com.holt.finance.biz.service.impl;

import com.holt.common.exception.BizException;
import com.holt.finance.biz.domain.Tenant;
import com.holt.finance.biz.mapper.TenantMapper;
import com.holt.finance.biz.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {
    final TenantMapper tenantMapper;

    /**
     * 创建租户
     *
     * @return 租户ID
     */
    @Override
    public long add() {
        Tenant tenant = new Tenant();
        tenant.initDefault();
        if (tenantMapper.insert(tenant) == 0) {
            throw new BizException("创建租户失败");
        }
        return tenant.getId();
    }
}
