package com.machugit.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.machugit"})
@MapperScan({"com.machugit.mappers"})
@EnableTransactionManagement
@EnableScheduling
@EnableFeignClients(basePackages = {"com.machugit.feign"})
public class BilbilAdminRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(BilbilAdminRunApplication.class,args);
    }
}

