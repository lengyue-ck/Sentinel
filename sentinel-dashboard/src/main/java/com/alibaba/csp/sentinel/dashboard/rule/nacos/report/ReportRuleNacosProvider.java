package com.alibaba.csp.sentinel.dashboard.rule.nacos.report;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.report.MailConfig;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.report.ReportEntity;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReportRuleNacosProvider {

    @Autowired
    private ConfigService configService;
    @Autowired
    private Converter<String, List<ReportEntity>> converter;


    public List<ReportEntity> getRules() throws Exception {
        String rules = configService.getConfig("rbd" + NacosConfigUtil.REPORT_DATA_ID_POSTFIX,
                NacosConfigUtil.GROUP_ID, NacosConfigUtil.TIMEOUT);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return converter.convert(rules);
    }

    public MailConfig getMailCOnfig() throws Exception {
        String rules = configService.getConfig("rbd" + NacosConfigUtil.MAIL_DATA_ID_POSTFIX,
                NacosConfigUtil.GROUP_ID, NacosConfigUtil.TIMEOUT);

        return JSON.parseObject(rules, MailConfig.class);
    }
}
