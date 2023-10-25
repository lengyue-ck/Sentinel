package com.alibaba.csp.sentinel.dashboard.rule.nacos.report;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.report.MailConfig;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.report.ReportEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportRuleNacosPublisher {

    @Autowired
    private ConfigService configService;
    @Autowired
    private Converter<List<ReportEntity>, String> converter;


    public void publish(List<ReportEntity> rules) throws Exception {
        configService.publishConfig("rbd" + NacosConfigUtil.REPORT_DATA_ID_POSTFIX,
                NacosConfigUtil.GROUP_ID, converter.convert(rules));
    }

    public void publish(MailConfig config) throws Exception {
        configService.publishConfig("rbd" + NacosConfigUtil.MAIL_DATA_ID_POSTFIX,
                NacosConfigUtil.GROUP_ID, JSON.toJSONString(config));
    }
}
