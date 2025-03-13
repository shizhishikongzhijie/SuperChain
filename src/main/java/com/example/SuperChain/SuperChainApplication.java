package com.example.SuperChain;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author shizhishi
 */
@SpringBootApplication
@EnableAsync // 开启异步注解功能
@EnableScheduling //注解，以启用Spring的定时任务功能。
@ServletComponentScan(value = {"com.example.SuperChain.filter"})
@MapperScan("com.example.SuperChain.mapper")
public class SuperChainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SuperChainApplication.class, args);
    }
}