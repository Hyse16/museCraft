package com.musecraft.brand;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.musecraft.brand", "com.musecraft.common"})
public class BrandApplication {
    public static void main(String[] args) {
        SpringApplication.run(BrandApplication.class, args);
    }
}
