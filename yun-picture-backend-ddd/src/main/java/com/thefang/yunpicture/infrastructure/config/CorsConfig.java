package com.thefang.yunpicture.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description 全局跨域配置
 * @Author Thefang
 * @Create 2024/12/8
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送 cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 pattern, 否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                // 放行哪些请求方式
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 放行哪些请求头
                .allowedHeaders("*")
                // 暴露哪些响应头
                .exposedHeaders("*");
    }
}
