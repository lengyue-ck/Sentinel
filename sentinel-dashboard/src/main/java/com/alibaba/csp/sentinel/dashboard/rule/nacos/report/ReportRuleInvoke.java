package com.alibaba.csp.sentinel.dashboard.rule.nacos.report;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.report.ReportEntity;
import com.alibaba.csp.sentinel.dashboard.repository.metric.MetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReportRuleInvoke {
    @Autowired
    private MetricsRepository<MetricEntity> metricStore;
    @Autowired
    private JavaMailSenderImpl javaMailSender;

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

        if (template.contains("${SUCCESS_COUNT}")) {
            long sumSuccessQps = entities.stream().mapToLong(MetricEntity::getSuccessQps).sum();
            template = template.replace("${SUCCESS_COUNT}", String.valueOf(sumSuccessQps));
        }

        if (template.contains("${EXCEPTION_COUNT}")) {
            long sumExceptionQps = entities.stream().mapToLong(MetricEntity::getExceptionQps).sum();
            template = template.replace("${EXCEPTION_COUNT}", String.valueOf(sumExceptionQps));
        }

        if (template.contains("${BLOCK_COUNT}")) {
            long sumBlockQps = entities.stream().mapToLong(MetricEntity::getBlockQps).sum();
            template = template.replace("${BLOCK_COUNT}", String.valueOf(sumBlockQps));
        }

        if (template.contains("${PASS_COUNT}")) {
            long sumPassQps = entities.stream().mapToLong(MetricEntity::getPassQps).sum();
            template = template.replace("${PASS_COUNT}", String.valueOf(sumPassQps));
        }

        if (template.contains("${RT}")) {
            double sumRt = entities.stream().mapToDouble(MetricEntity::getRt).sum();
            template = template.replace("${RT}", String.valueOf(sumRt / entities.size()));
        }

        if (template.contains("${METHOD}")) {
            if (reportEntity.getMethod() == ReportEntity.EXCEPTION_QPS) {
                template = template.replace("${METHOD}", "异常次数");
            } else if (reportEntity.getMethod() == ReportEntity.BLOCK_QPS) {
                template = template.replace("${METHOD}", "阻塞次数");
            } else if (reportEntity.getMethod() == ReportEntity.PASS_QPS) {
                template = template.replace("${METHOD}", "通过次数");
            } else if (reportEntity.getMethod() == ReportEntity.RT) {
                template = template.replace("${METHOD}", "响应时间");
            } else if (reportEntity.getMethod() == ReportEntity.SUCCESS_QPS) {
                template = template.replace("${METHOD}", "成功次数");
            } else if (reportEntity.getMethod() == ReportEntity.TOTAL_QPS) {
                template = template.replace("${METHOD}", "总次数");
            }
        }

        if (template.contains("${CONDITION}")) {
            if (reportEntity.getCondition() == ReportEntity.GT) {
                template = template.replace("${CONDITION}", "大于");
            } else if (reportEntity.getCondition() == ReportEntity.LT) {
                template = template.replace("${CONDITION}", "小于");
            }
        }


//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setSubject("告警通知");
//        message.setText(template);
//        message.setTo("1666888816@qq.com");
//        message.setFrom(javaMailSender.getUsername());
//        javaMailSender.send(message);


        System.out.println(template);
    }

    @Async
    public void asyncInvoke(ReportEntity entity) {
        long endTime = System.currentTimeMillis() - 1000 * 30;//收集信息有延迟

        long startTime = endTime - entity.getInterval() * 1000L;

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
