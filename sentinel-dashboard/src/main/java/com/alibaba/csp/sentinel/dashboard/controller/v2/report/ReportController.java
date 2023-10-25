package com.alibaba.csp.sentinel.dashboard.controller.v2.report;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.report.ReportEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.dashboard.domain.vo.MetricVo;
import com.alibaba.csp.sentinel.dashboard.repository.metric.MetricsRepository;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.other.IncrNacosRule;
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
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping(value = "/sentinel-service/report", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {
    @Autowired
    private ReportRuleNacosProvider provider;
    @Autowired
    private ReportRuleNacosPublisher publisher;
    @Autowired
    private MetricsRepository<MetricEntity> metricStore;
    @Autowired
    private IncrNacosRule incrNacosRule;

    @Scheduled(fixedRate = 5000) // 每隔5秒执行一次
    public void test() {
        long endTime = System.currentTimeMillis() - 1000 * 30;//收集信息有延迟
        long startTime = endTime - 1000 * 10;
        String app = "graee68d";
        String resource = "/pc-service/api/hello";
        List<MetricEntity> entities = metricStore.queryByAppAndResourceBetween(app, resource, startTime, endTime);

        long sumPassQps = entities.stream().mapToLong(MetricEntity::getPassQps).sum();
//        long sumSuccessQps = entities.stream()
//                .mapToLong(MetricEntity::getPassQps)
//                .sum();
        System.out.println("sumSuccessQps: " + sumPassQps);
        System.out.println("entities: " + entities.size());
    }

    @PostMapping("/create")
    public Result<?> create(@RequestBody ReportEntity entity) throws Exception {
        entity.setId(incrNacosRule.getAndIncrementId());
        List<ReportEntity> rules = provider.getRules(entity.getApp());

        rules.add(entity);

        publisher.publish(entity.getApp(), rules);
        return Result.ofSuccess(entity);
    }
}
