package com.alibaba.csp.sentinel.dashboard.controller.v2;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.dashboard.repository.rule.InMemoryRuleRepositoryAdapter;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class IdSettingAspect {
    @Before("execution(* com.alibaba.csp.sentinel.dashboard.repository.rule.InMemoryRuleRepositoryAdapter.save(..)) && args(entity)")
    public void setIdIfNull(RuleEntity entity) {

        if (entity.getId() == null) {
            System.out.println("设置id");
            // 在这里设置id的值，可以调用自定义的方法
//            entity.setId(customNextIdLogic());
        }
    }

}
