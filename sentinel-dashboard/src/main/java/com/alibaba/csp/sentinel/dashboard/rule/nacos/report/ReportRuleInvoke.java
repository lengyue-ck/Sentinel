package com.alibaba.csp.sentinel.dashboard.rule.nacos.report;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.report.MailConfig;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.report.ReportEntity;
import com.alibaba.csp.sentinel.dashboard.repository.metric.MetricsRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class ReportRuleInvoke {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ReportRuleInvoke.class);

    @Autowired
    private ReportRuleNacosProvider provider;
    @Autowired
    private MetricsRepository<MetricEntity> metricStore;
    @Autowired
    private JavaMailSenderImpl javaMailSender;

    //支持模版配置

    /**
     * ${THRESHOLD} 阈值
     * ${APP} 组件名称
     * ${RESOURCE} 资源名称
     * ${TIME} 时间
     * ${COUNT} 总请求次数
     * ${SUCCESS_COUNT} 成功请求数
     * ${EXCEPTION_COUNT} 异常数
     * ${BLOCK_COUNT} 拒绝数
     * ${RT} 平均响应时间
     * ${METHOD} 统计方法->[异常次数,阻塞次数,通过次数,响应时间,成功次数,总请求次数]
     * ${CONDITION} 提交 ->[大于,小于]
     */
    private void report(List<MetricEntity> entities, ReportEntity reportEntity) throws Exception {
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
                template = template.replace("${METHOD}", "拒接次数");
            } else if (reportEntity.getMethod() == ReportEntity.PASS_QPS) {
                template = template.replace("${METHOD}", "通过次数");
            } else if (reportEntity.getMethod() == ReportEntity.RT) {
                template = template.replace("${METHOD}", "响应时间");
            } else if (reportEntity.getMethod() == ReportEntity.SUCCESS_QPS) {
                template = template.replace("${METHOD}", "成功次数");
            } else if (reportEntity.getMethod() == ReportEntity.TOTAL_QPS) {
                template = template.replace("${METHOD}", "总请求次数");
            }
        }

        if (template.contains("${CONDITION}")) {
            if (reportEntity.getCondition() == ReportEntity.GT) {
                template = template.replace("${CONDITION}", "大于");
            } else if (reportEntity.getCondition() == ReportEntity.LT) {
                template = template.replace("${CONDITION}", "小于");
            }
        }
        MailConfig mailCOnfig = provider.getMailCOnfig();


        javaMailSender.setHost(mailCOnfig.getHost());
        javaMailSender.setPort(Integer.parseInt(mailCOnfig.getPort()));
        javaMailSender.setUsername(mailCOnfig.getUsername());
        javaMailSender.setPassword(mailCOnfig.getPassword());
        for (String s : reportEntity.getEmail().split(",")) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject("告警通知");
            message.setText(template);
            message.setTo(s);
            message.setFrom(Objects.requireNonNull(javaMailSender.getUsername()));
            javaMailSender.send(message);
        }
        System.out.println(template);
    }

    @Async
    public void asyncInvoke(ReportEntity entity) throws Exception {
        long endTime = System.currentTimeMillis() - 1000 * 30;//收集信息有延迟

        long startTime = endTime - Math.max(entity.getInterval(), 1) * 1000L;

        List<MetricEntity> entities = metricStore.queryByAppAndResourceBetween(entity.getApp(), entity.getResource(), startTime, endTime);

        if (entity.getMethod() == ReportEntity.TOTAL_QPS) {
            long sumPassQps = entities.stream().mapToLong(MetricEntity::getPassQps).sum();
            long sumBlockQps = entities.stream().mapToLong(MetricEntity::getBlockQps).sum();
            logger.info("总共的QPS" + (sumBlockQps + sumPassQps));
            if (sumPassQps + sumBlockQps > entity.getCount() && entity.getCondition() == ReportEntity.GT) {
                report(entities, entity);
            } else if (sumPassQps + sumBlockQps < entity.getCount() && entity.getCondition() == ReportEntity.LT) {
                report(entities, entity);
            }
        } else if (entity.getMethod() == ReportEntity.SUCCESS_QPS) {
            long sumSuccessQps = entities.stream().mapToLong(MetricEntity::getSuccessQps).sum();
            logger.info("成功的QPS" + sumSuccessQps);
            if (sumSuccessQps > entity.getCount() && entity.getCondition() == ReportEntity.GT) {
                report(entities, entity);
            } else if (sumSuccessQps < entity.getCount() && entity.getCondition() == ReportEntity.LT) {
                report(entities, entity);
            }
        } else if (entity.getMethod() == ReportEntity.EXCEPTION_QPS) {

            long sumExceptionQps = entities.stream().mapToLong(MetricEntity::getExceptionQps).sum();
            logger.info("异常的QPS" + sumExceptionQps);
            if (sumExceptionQps > entity.getCount() && entity.getCondition() == ReportEntity.GT) {
                report(entities, entity);
            } else if (sumExceptionQps < entity.getCount() && entity.getCondition() == ReportEntity.LT) {
                report(entities, entity);
            }
        } else if (entity.getMethod() == ReportEntity.BLOCK_QPS) {
            long sumBlockQps = entities.stream().mapToLong(MetricEntity::getBlockQps).sum();
            logger.info("阻塞的QPS" + sumBlockQps);
            if (sumBlockQps > entity.getCount() && entity.getCondition() == ReportEntity.GT) {
                report(entities, entity);
            } else if (sumBlockQps < entity.getCount() && entity.getCondition() == ReportEntity.LT) {
                report(entities, entity);
            }
        } else if (entity.getMethod() == ReportEntity.RT) {
            double sumRt = entities.stream().mapToDouble(MetricEntity::getRt).sum();
            logger.info("响应时间" + (sumRt / entities.size()));
            if (sumRt / entities.size() > entity.getCount() && entity.getCondition() == ReportEntity.GT) {
                report(entities, entity);
            } else if (sumRt < entity.getCount() && entity.getCondition() == ReportEntity.LT) {
                report(entities, entity);
            }
        } else if (entity.getMethod() == ReportEntity.PASS_QPS) {

            long sumPassQps = entities.stream().mapToLong(MetricEntity::getPassQps).sum();
            logger.info("通过的QPS" + sumPassQps);
            if (sumPassQps > entity.getCount() && entity.getCondition() == ReportEntity.GT) {
                report(entities, entity);
            } else if (sumPassQps < entity.getCount() && entity.getCondition() == ReportEntity.LT) {
                report(entities, entity);
            }
        }
    }
}
