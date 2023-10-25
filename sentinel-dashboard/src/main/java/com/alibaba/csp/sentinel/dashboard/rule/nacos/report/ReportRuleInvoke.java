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

    //支持模版配置

    /**
     * ${APP} 组件名称
     * ${RESOURCE} 资源名称
     * ${TIME} 时间
     * ${COUNT} 次数
     */
    private void report(List<MetricEntity> entities, ReportEntity reportEntity) {
        if (entities.isEmpty()) {
            return;
        }
        MetricEntity entity = entities.get(0);
        String template = reportEntity.getTemplate();

        template = template.replace("${THRESHOLD}", String.valueOf(reportEntity.getCount()));
        template = template.replace("${APP}", entity.getApp());
        template = template.replace("${RESOURCE}", entity.getResource());
        template = template.replace("${TIME}", String.valueOf(entity.getTimestamp()));
        if (template.contains("${COUNT}")) {
            long sumPassQps = entities.stream().mapToLong(MetricEntity::getPassQps).sum();
            long sumBlockQps = entities.stream().mapToLong(MetricEntity::getBlockQps).sum();
            template = template.replace("${COUNT}", String.valueOf(sumPassQps + sumBlockQps));
        }

        System.out.println(template);
    }

    @Async
    public void asyncInvoke(ReportEntity entity) {
        long endTime = System.currentTimeMillis() - 1000 * 30;//收集信息有延迟
        long startTime = endTime - 1000 * 10;

        List<MetricEntity> entities = metricStore.queryByAppAndResourceBetween(entity.getApp(), entity.getResource(), startTime, endTime);

        if (entity.getMethod() == ReportEntity.TOTAL_QPS) {
            long sumPassQps = entities.stream().mapToLong(MetricEntity::getPassQps).sum();
            long sumBlockQps = entities.stream().mapToLong(MetricEntity::getBlockQps).sum();
            if (sumPassQps + sumBlockQps > entity.getCount() && entity.getCondition() == ReportEntity.GT) {
                report(entities, entity);
            } else if (sumPassQps + sumBlockQps < entity.getCount() && entity.getCondition() == ReportEntity.LT) {
                report(entities, entity);
            }
        } else if (entity.getMethod() == ReportEntity.SUCCESS_QPS) {
            long sumSuccessQps = entities.stream().mapToLong(MetricEntity::getSuccessQps).sum();
            if (sumSuccessQps > entity.getCount() && entity.getCondition() == ReportEntity.GT) {
                report(entities, entity);
            } else if (sumSuccessQps < entity.getCount() && entity.getCondition() == ReportEntity.LT) {
                report(entities, entity);
            }
        } else if (entity.getMethod() == ReportEntity.EXCEPTION_QPS) {
            long sumExceptionQps = entities.stream().mapToLong(MetricEntity::getExceptionQps).sum();
            if (sumExceptionQps > entity.getCount() && entity.getCondition() == ReportEntity.GT) {
                report(entities, entity);
            } else if (sumExceptionQps < entity.getCount() && entity.getCondition() == ReportEntity.LT) {
                report(entities, entity);
            }
        } else if (entity.getMethod() == ReportEntity.BLOCK_QPS) {
            long sumBlockQps = entities.stream().mapToLong(MetricEntity::getBlockQps).sum();
            if (sumBlockQps > entity.getCount() && entity.getCondition() == ReportEntity.GT) {
                report(entities, entity);
            } else if (sumBlockQps < entity.getCount() && entity.getCondition() == ReportEntity.LT) {
                report(entities, entity);
            }
        } else if (entity.getMethod() == ReportEntity.RT) {
            double sumRt = entities.stream().mapToDouble(MetricEntity::getRt).sum();
            if (sumRt / entities.size() > entity.getCount() && entity.getCondition() == ReportEntity.GT) {
                report(entities, entity);
            } else if (sumRt < entity.getCount() && entity.getCondition() == ReportEntity.LT) {
                report(entities, entity);
            }
        } else if (entity.getMethod() == ReportEntity.PASS_QPS) {
            long sumPassQps = entities.stream().mapToLong(MetricEntity::getPassQps).sum();
            if (sumPassQps > entity.getCount() && entity.getCondition() == ReportEntity.GT) {
                report(entities, entity);
            } else if (sumPassQps < entity.getCount() && entity.getCondition() == ReportEntity.LT) {
                report(entities, entity);
            }
        }
    }
}
