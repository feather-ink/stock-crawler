package com.crawler.demo;

import com.crawler.CrawlerDemo;
import com.crawler.pojo.proxy.ProxyUrl;
import com.crawler.service.ProxyUrlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CrawlerDemo.class)
public class ServiceTest {

}
