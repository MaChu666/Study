package com.machugit.admin.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    @Value("${project.folder:}")
    private String projectFolder;

    @Resource
    private Appinterceptor appinterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(appinterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/videos/**", "/images/**", "/sysSetting/loadThemes");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (projectFolder != null && !projectFolder.isEmpty()) {
            registry.addResourceHandler("/videos/**")
                    .addResourceLocations("file:" + projectFolder + "/videos/");
            registry.addResourceHandler("/images/**")
                    .addResourceLocations("file:" + projectFolder + "/images/");
        }
    }
}