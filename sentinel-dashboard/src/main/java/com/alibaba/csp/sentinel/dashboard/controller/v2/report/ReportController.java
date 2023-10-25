package com.alibaba.csp.sentinel.dashboard.controller.v2.report;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.repository.metric.MetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/sentinel-service/report")
public class ReportController {
    @Autowired
    private MetricsRepository<MetricEntity> metricStore;


    @Scheduled(fixedRate = 5000) // 每隔5秒执行一次
    public void test() {
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 1000 * 10;
        String app = "graee68d";
        List<MetricEntity> entities = metricStore.queryByAppAndResourceBetween(app, "/pc-service/api/hello", startTime, endTime);

        long sumSuccessQps = entities.stream()
                .mapToLong(MetricEntity::getPassQps)
                .sum();
        System.out.println("sumSuccessQps: " + sumSuccessQps);
    }
}
