package com.crawler.util.request_get;

import com.crawler.pojo.url.SeedUrl;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.springframework.stereotype.Component;

import java.net.URI;

@Slf4j
@Component
public class HttpGetBuilder {

    private HttpGet setHttpGetConfig(HttpGet httpGet) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(1000)                    //创建连接的最长时间
                .setConnectionRequestTimeout(500)           //设置获取连接的最长时间
                .setSocketTimeout(10000)                    //设置数据传输的最长时间
                .build();
        httpGet.setConfig(config);
        return httpGet;
    }

    //如有Referer、Cookie则填充
    private HttpGet createHttpGet(SeedUrl seedUrl){
        HttpGet httpGet = new HttpGet(seedUrl.getLink());
        if (seedUrl.getReferer() != null && seedUrl.getReferer().length() > 0){
            httpGet.setHeader("referer", seedUrl.getReferer());
            log.info("referer：{}",seedUrl.getReferer());
        }
        if (seedUrl.getCookie() != null && seedUrl.getCookie().length() > 0){
            httpGet.setHeader("cookie", seedUrl.getCookie());
            log.info("cookie：{}",seedUrl.getCookie());
        }
        return httpGet;
    }

    //构造一个HttpGet请求
    public HttpGet obtainHttpGet(SeedUrl seedUrl){
        log.info("正在访问：{}",seedUrl.getLink());
        HttpGet httpGet = createHttpGet(seedUrl);
        return setHttpGetConfig(httpGet);
    }

}
