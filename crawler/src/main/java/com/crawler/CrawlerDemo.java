package com.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CrawlerDemo {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerDemo.class, args);
    }

}
