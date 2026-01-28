package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ru.kata.spring.boot_security.demo.converter.StringToRoleConverter;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private final StringToRoleConverter stringToRoleConverter;

    public MvcConfig(StringToRoleConverter stringToRoleConverter) {
        this.stringToRoleConverter = stringToRoleConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToRoleConverter);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/admin").setViewName("admin/users");
        registry.addViewController("/user").setViewName("user");
        registry.addViewController("/login").setViewName("login");
    }
}

