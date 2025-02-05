/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.report.ReportEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class NacosConfig {

    @Bean
    public Converter<List<ReportEntity>, String> reportRuleEntityEncoder() {
        return JSON::toJSONString;
    }

    @Bean
    public Converter<String, List<ReportEntity>> reportRuleEntityDecoder() {
        return s -> JSON.parseArray(s, ReportEntity.class);
    }
    @Bean
    public Converter<List<FlowRuleEntity>, String> flowRuleEntityEncoder() {
        return JSON::toJSONString;
    }

    @Bean
    public Converter<String, List<FlowRuleEntity>> flowRuleEntityDecoder() {
        return s -> JSON.parseArray(s, FlowRuleEntity.class);
    }

    @Bean
    public Converter<List<DegradeRuleEntity>, String> degradeRuleEntityEncoder() {
        return JSON::toJSONString;
    }

    @Bean
    public Converter<String, List<DegradeRuleEntity>> degradeRuleEntityDecoder() {
        return s -> JSON.parseArray(s, DegradeRuleEntity.class);
    }

    @Bean
    public Converter<Long, String> idEncoder() {
        return String::valueOf;
    }

    @Bean
    public Converter<String, Long> idDecoder() {
        return s -> {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                return 0L;
            }
        };
    }

    // com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule 字段为 intervalSec
    // com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity 字段为 interval
    // 序列化的JSON数据持久化到nacos中，时间间隔为 interval
    // 而客户端从nacos获取的数据中，时间间隔为 intervalSec
    // 为了兼容，这里做了转换
    @Bean
    public Converter<List<GatewayFlowRuleEntity>, String> flowGatewayRuleEntityEncoder() {
        return s -> JSON.toJSONString(s).replaceAll("interval", "intervalSec");
    }

    @Bean
    public Converter<String, List<GatewayFlowRuleEntity>> flowGatewayeRuleEntityDecoder() {
        return s -> JSON.parseArray(s.replaceAll("intervalSec", "interval"), GatewayFlowRuleEntity.class);
    }

    @Bean
    public ConfigService nacosConfigService() throws Exception {
        return ConfigFactory.createConfigService("nacos.spring-cloud-system.svc.cluster.local:8848");
    }
}
