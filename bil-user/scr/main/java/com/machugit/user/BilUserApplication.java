package com.machugit.user;
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
public class BilUserApplication {
    public static void main(String[] args) { SpringApplication.run(BilUserApplication.class, args); }
}