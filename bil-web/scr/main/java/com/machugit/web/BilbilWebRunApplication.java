package com.machugit.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.machugit"})
@MapperScan({"com.machugit.mappers"})
public class BilbilWebRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(BilbilWebRunApplication.class, args);
    }
}
