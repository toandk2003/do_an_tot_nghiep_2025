package org.yenln8.ChatApp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.yenln8.ChatApp.interceptor.LocaleInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LocaleInterceptor localeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Đăng ký LocaleInterceptor cho tất cả các request
        registry.addInterceptor(localeInterceptor)
                .addPathPatterns("/**") // Áp dụng cho tất cả đường dẫn
                .excludePathPatterns("/static/**", "/css/**", "/js/**", "/images/**"); // Loại trừ static resources
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:5173"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // hoặc "*"
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}