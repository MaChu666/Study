package com.machugit.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class SentinelGatewayConfig {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public SentinelGatewayConfig(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                  ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    @Bean
    @Order(-1)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    @PostConstruct
    public void init() {
        initCustomizedApis();
        initGatewayFlowRules();
    }

    private void initCustomizedApis() {
        Set<ApiDefinition> definitions = new HashSet<>();

        ApiDefinition authApi = new ApiDefinition("auth_api");
        authApi.setPredicateItems(new HashSet<>());
        authApi.getPredicateItems().add(new ApiPathPredicateItem()
                .setPattern("/api/auth/**")
                .setMatchStrategy(SentinelGatewayConstants.PARAM_MATCH_STRATEGY_PREFIX));
        authApi.getPredicateItems().add(new ApiPathPredicateItem()
                .setPattern("/api/account/**")
                .setMatchStrategy(SentinelGatewayConstants.PARAM_MATCH_STRATEGY_PREFIX));
        definitions.add(authApi);

        ApiDefinition videoApi = new ApiDefinition("video_api");
        videoApi.setPredicateItems(new HashSet<>());
        videoApi.getPredicateItems().add(new ApiPathPredicateItem()
                .setPattern("/api/video/**")
                .setMatchStrategy(SentinelGatewayConstants.PARAM_MATCH_STRATEGY_PREFIX));
        definitions.add(videoApi);

        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }

    private void initGatewayFlowRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        rules.add(new GatewayFlowRule("auth_api").setCount(50).setIntervalSec(1));
        rules.add(new GatewayFlowRule("video_api").setCount(100).setIntervalSec(1));
        GatewayRuleManager.loadRules(rules);
    }
}