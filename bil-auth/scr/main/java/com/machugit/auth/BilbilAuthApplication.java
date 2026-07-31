package com.machugit.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@MapperScan(basePackages = {"com.machugit.mappers"})
@EnableTransactionManagement
@EnableFeignClients(basePackages = {"com.machugit.feign"})
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.machugit"},
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.machugit\\.es\\..*")
    })
public class BilbilAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(BilbilAuthApplication.class, args);
    }
}