package com.machugit.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(scanBasePackages = {"com.machugit"})
@MapperScan({"com.machugit.mappers"})
public class BilbilAdminRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(BilbilAdminRunApplication.class,args);
    }
}
