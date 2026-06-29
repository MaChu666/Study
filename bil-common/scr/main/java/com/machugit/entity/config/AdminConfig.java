package com.machugit.entity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "admin", name = "account")
public class AdminConfig {

    @Value("${admin.account}")
    private String account;

    @Value("${admin.password}")
    private String password;

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
