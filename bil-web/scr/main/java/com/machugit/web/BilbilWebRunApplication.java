package com.machugit.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.machugit"})
@MapperScan({"com.machugit.mappers"})
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
@EnableElasticsearchRepositories(basePackages = {"com.machugit.es"})
public class BilbilWebRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(BilbilWebRunApplication.class, args);
    }
}
