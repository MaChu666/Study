package com.machugit.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.machugit.gateway",
    "com.machugit.component",
    "com.machugit.entity",
    "com.machugit.redis",
    "com.machugit.exception",
    "com.machugit.config"
})
@EnableDiscoveryClient
public class BilbilGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(BilbilGatewayApplication.class, args);
    }
}