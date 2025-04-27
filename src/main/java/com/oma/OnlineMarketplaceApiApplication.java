package com.oma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OnlineMarketplaceApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineMarketplaceApiApplication.class, args);
    }

}
