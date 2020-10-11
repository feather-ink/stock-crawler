package com.crawler.demo;

import com.crawler.CrawlerDemo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;



@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CrawlerDemo.class)
public class InitTest {

    @Test
    public void testInitDatabases(){
        System.out.println("初始化测试");
    }

}
