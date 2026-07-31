package com.machugit.video;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@SpringBootApplication(scanBasePackages = {"com.machugit"})
@MapperScan({"com.machugit.mappers"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.machugit.feign"})
@EnableTransactionManagement
public class BilVideoApplication {
    public static void main(String[] args) {
        SpringApplication.run(BilVideoApplication.class, args);
    }
}