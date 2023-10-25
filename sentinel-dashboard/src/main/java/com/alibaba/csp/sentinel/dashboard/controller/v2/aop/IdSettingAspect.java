package com.alibaba.csp.sentinel.dashboard.controller.v2.aop;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.other.IncrNacosRule;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.nacos.api.config.ConfigService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class IdSettingAspect {
    @Autowired
    private IncrNacosRule incrNacosRule;

    //全局共用一个自增id，就不需要对每个规则类型都设置一个自增id了
    @Before("execution(* com.alibaba.csp.sentinel.dashboard.repository.rule.InMemoryRuleRepositoryAdapter.save(..)) && args(entity)")
    public void setIdIfNull(RuleEntity entity) {
        if (entity.getId() == null) {
            try {
                entity.setId(incrNacosRule.getAndIncrementId());
            } catch (Exception ignored) {
            }
        }
    }
}
