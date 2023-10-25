package com.alibaba.csp.sentinel.dashboard.controller.v2.report;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.report.ReportEntity;
import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.other.IncrNacosRule;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.report.ReportRuleInvoke;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.report.ReportRuleNacosProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.report.ReportRuleNacosPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/sentinel-service/report", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {
    @Autowired
    private ReportRuleNacosProvider provider;
    @Autowired
    private ReportRuleNacosPublisher publisher;
    @Autowired
    private ReportRuleInvoke reportRuleInvoke;
    @Autowired
    private IncrNacosRule incrNacosRule;

    @Scheduled(fixedRate = 10000) // 每隔5秒执行一次
    public void scheduled() throws Exception {
        List<ReportEntity> rules = provider.getRules();
        for (ReportEntity rule : rules) {
            reportRuleInvoke.asyncInvoke(rule);
        }
    }

    @PostMapping("/create")
    public Result<?> create(@RequestBody ReportEntity entity) throws Exception {
        entity.setId(incrNacosRule.getAndIncrementId());
        List<ReportEntity> rules = provider.getRules();

        rules.add(entity);

        publisher.publish(rules);
        return Result.ofSuccess(entity);
    }
}
