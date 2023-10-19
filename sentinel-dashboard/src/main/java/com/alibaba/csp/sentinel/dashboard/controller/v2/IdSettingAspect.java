package com.alibaba.csp.sentinel.dashboard.controller.v2;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
public class IdSettingAspect {
    @Autowired
    private ConfigService configService;

    private Long getAndIncrementId() throws Exception {
        String id = configService.getConfig("id", NacosConfigUtil.GROUP_ID, 3000);
        System.out.println("id: " + id);
        AtomicLong ids = new AtomicLong(Long.parseLong(id));
        long newId = ids.incrementAndGet();
        configService.publishConfig("id", NacosConfigUtil.GROUP_ID, String.valueOf(newId));
        return newId;
    }


    //全局共用一个自增id，就不需要对每个规则类型都设置一个自增id了
    @Before("execution(* com.alibaba.csp.sentinel.dashboard.repository.rule.InMemoryRuleRepositoryAdapter.save(..)) && args(entity)")
    public void setIdIfNull(RuleEntity entity) {

        if (entity.getId() == null) {
            try {
                entity.setId(getAndIncrementId());
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

}
