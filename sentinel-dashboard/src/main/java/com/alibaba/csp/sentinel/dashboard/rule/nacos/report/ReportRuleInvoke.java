package com.alibaba.csp.sentinel.dashboard.rule.nacos.report;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.report.ReportEntity;
import com.alibaba.csp.sentinel.dashboard.repository.metric.MetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReportRuleInvoke {
    @Autowired
    private MetricsRepository<MetricEntity> metricStore;

    @Async
    public void asyncInvoke(ReportEntity entity) {
        long endTime = System.currentTimeMillis() - 1000 * 30;//收集信息有延迟
        long startTime = endTime - 1000 * 10;

        List<MetricEntity> entities = metricStore.queryByAppAndResourceBetween(entity.getApp(), entity.getResource(), startTime, endTime);

        if (entity.getMethod() == ReportEntity.TOTAL_QPS) {
            long sumPassQps = entities.stream().mapToLong(MetricEntity::getPassQps).sum();
            long sumBlockQps = entities.stream().mapToLong(MetricEntity::getBlockQps).sum();
            if (sumPassQps + sumBlockQps > entity.getCount() && entity.getCondition() == ReportEntity.GT) {
                System.out.println("满足告警规则sumSuccessQps: " + sumPassQps);
            }else if (sumPassQps + sumBlockQps < entity.getCount() && entity.getCondition() == ReportEntity.LT) {
                System.out.println("满足告警规则sumSuccessQps: " + sumPassQps);
            }
        }

    }
}
