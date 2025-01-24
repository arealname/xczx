package com.cwj.content;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * <p>
 *     系统管理启动类
 * </p>
 *
 * @Description:
 */

@SpringBootApplication(scanBasePackages = "com.cwj.*.*")
@MapperScan("com.cwj.content.mapper")
@EnableFeignClients(basePackages = "com.cwj.content.feign")
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class,args);
    }
}