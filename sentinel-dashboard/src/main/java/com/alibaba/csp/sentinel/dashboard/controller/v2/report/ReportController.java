package com.alibaba.csp.sentinel.dashboard.controller.v2.report;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.domain.vo.MetricVo;
import com.alibaba.csp.sentinel.dashboard.repository.metric.MetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping(value = "/sentinel-service/report")
public class ReportController {
    @Autowired
    private MetricsRepository<MetricEntity> metricStore;

    private Iterable<MetricVo> sortMetricVoAndDistinct(List<MetricVo> vos) {
        if (vos == null) {
            return null;
        }
        Map<Long, MetricVo> map = new TreeMap<>();
        for (MetricVo vo : vos) {
            MetricVo oldVo = map.get(vo.getTimestamp());
            if (oldVo == null || vo.getGmtCreate() > oldVo.getGmtCreate()) {
                map.put(vo.getTimestamp(), vo);
            }
        }
        return map.values();
    }
    @Scheduled(fixedRate = 5000) // 每隔5秒执行一次
    public void test() {
        long endTime = System.currentTimeMillis() - 1000 * 5;
        long startTime = endTime - 1000 * 10;
        String app = "graee68d";
        String resource = "/pc-service/api/hello";
        List<MetricEntity> entities = metricStore.queryByAppAndResourceBetween(app,resource , startTime, endTime);

        List<MetricVo> vos = MetricVo.fromMetricEntities(entities, resource);
        Iterable<MetricVo> vosSorted = sortMetricVoAndDistinct(vos);

        int sumSuccessQps = 0;
        for (MetricVo metricVo : vosSorted) {
            sumSuccessQps+=metricVo.getSuccessQps();
        }
//        long sumSuccessQps = entities.stream()
//                .mapToLong(MetricEntity::getPassQps)
//                .sum();
        System.out.println("sumSuccessQps: " + sumSuccessQps);
        System.out.println("entities: " + entities.size());
    }
}
