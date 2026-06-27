package com.machugit.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.machugit"},exclude = {DataSourceAutoConfiguration.class})
public class BilbilAdminRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(BilbilAdminRunApplication.class,args);
    }
}
