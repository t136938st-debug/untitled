package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 扫码点餐系统启动类
 */
@SpringBootApplication
@MapperScan("org.example.mapper")
@EnableScheduling // 开启定时任务（订单超时自动取消）
public class ScanOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScanOrderApplication.class, args);
        System.out.println("===== 扫码点餐系统启动成功 =====");
    }
}
