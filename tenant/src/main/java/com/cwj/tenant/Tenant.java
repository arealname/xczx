package com.cwj.tenant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Tenant {
    public static void main(String[] args) {
        SpringApplication.run(Tenant.class,args);
        System.out.println("Hello world!");
    }
}