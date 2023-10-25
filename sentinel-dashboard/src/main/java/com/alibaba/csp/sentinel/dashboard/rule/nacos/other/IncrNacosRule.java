package com.alibaba.csp.sentinel.dashboard.rule.nacos.other;

import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IncrNacosRule {

    @Autowired
    private ConfigService configService;
    @Autowired
    private Converter<String, Long> converter;
    @Autowired
    private Converter<Long, String> converter2;

    public Long getAndIncrementId() throws Exception {
        String oldId = configService.getConfig(NacosConfigUtil.INCR_ID, NacosConfigUtil.GROUP_ID, NacosConfigUtil.TIMEOUT);
        Long newId = converter.convert(oldId) + 1L;
        configService.publishConfig(NacosConfigUtil.INCR_ID, NacosConfigUtil.GROUP_ID, converter2.convert(newId));
        return newId;
    }
}
