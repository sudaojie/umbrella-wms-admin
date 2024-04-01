package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动程序
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableAsync
@EnableScheduling
public class WmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(WmsApplication.class, args);
    }
}
