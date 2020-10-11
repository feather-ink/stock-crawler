package com.crawler.util.httpclient;

import com.crawler.pojo.Browser;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.stereotype.Component;


@Component
public class HttpClientUtils {

    private static HttpClientUtils instance = new HttpClientUtils();

    private PoolingHttpClientConnectionManager connectionManager;

    private HttpClientBuilder httpClientBuilder;

    private CloseableHttpClient httpClient;


    private HttpClientUtils() {
        this.connectionManager = new PoolingHttpClientConnectionManager();
        
        this.connectionManager.setMaxTotal(100);

        this.connectionManager.setDefaultMaxPerRoute(10);

        this.httpClientBuilder = HttpClientBuilder.create().setConnectionManager(this.connectionManager);

        this.httpClient = httpClientBuilder
                .setUserAgent(Browser.FIREFOX.getUserAgent())
                .build();
    }

    public static CloseableHttpClient getInstance(){
        return instance.httpClient;
    }
}
