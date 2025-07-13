package com.holt;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class FinanceAdminApiApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(FinanceAdminApiApplication.class, args);
    }
}
