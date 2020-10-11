package com.crawler.download;

import com.crawler.custom_exception.IPUseUpException;
import com.crawler.pojo.proxy.ProxyPool;
import com.crawler.pojo.proxy.ProxyUrl;
import com.crawler.service.ProxyUrlService;
import com.crawler.util.httpclient.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ConnectException;


@Slf4j
@Component
public class RemoteConnect {

    private CloseableHttpResponse response;

    private ProxyUrlService urlService;

    private ProxyUrl proxyUrl;

    public RemoteConnect(ProxyUrlService urlService) {
        this.urlService = urlService;
    }

    //在无代理IP情况下，使用代理IP访问
    public CloseableHttpResponse connectRemote(HttpRequestBase httpRequest) throws InterruptedException, IPUseUpException {
        boolean flag = false;
        CloseableHttpClient httpClient = HttpClientUtils.getInstance();
        //循环获取可正常访问代理IP
        while (!flag) {
            ProxyPool proxyPool = ProxyPool.getInstance();
            //判断IP池是否为空，用作于有序使用数据库中代理IP或爬取网页IP时已至最后一页
            if (proxyPool.isEmpty()){
                throw new IPUseUpException("代理IP已用完");
            }
            //获取代理IP
            ProxyUrl proxyUrl = proxyPool.takeProxyPool();
            HttpHost httpHost = new HttpHost(proxyUrl.getHost(), proxyUrl.getPort());
            try {
                response = httpClient.execute(httpHost, httpRequest);
            } catch (IOException e) {
                //数据获取异常，更换代理IP
                flag = false;
            }
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                //标记该代理IP获取成功获取数据，可再次使用
                this.proxyUrl = proxyUrl;
                //数据获取成功
                flag = true;
            }
            //修改代理IP在数据库中的权重
            updateDatabaseUrl(proxyUrl, flag);
        }
        return response;
    }

    //使用代理IP访问
    public CloseableHttpResponse connectRemote(HttpRequestBase httpRequest, ProxyUrl proxyUrl) throws ConnectException {
        CloseableHttpClient httpClient = HttpClientUtils.getInstance();
        HttpHost httpHost = new HttpHost(proxyUrl.getHost(), proxyUrl.getPort());
        try {
            response = httpClient.execute(httpHost, httpRequest);
        } catch (IOException e) {
            throw new ConnectException("连接失败!!!");
        }
        if (response == null && response.getStatusLine().getStatusCode() != 200) {
            throw new ConnectException("获取数据失败！！!");
        }
        return response;
    }

    /*
    修改代理IP在数据库中的权重。
     1、如果存在IP且访问成功，权重+1。
     2、如果存在IP且访问失败，权重-1，减至最小则删除。
     3、如果不存在IP且访问成功，存入IP。
     4、如果不存在IP且访问失败，无动作。
     */
    private void updateDatabaseUrl(ProxyUrl proxyUrl, boolean flag) {
        if (urlService.isExistProxyUrl(proxyUrl)) {
            if (flag) {
                proxyUrl.setCount(proxyUrl.getCount() + 1);
            } else {
                proxyUrl.setCount(proxyUrl.getCount() - 1);
                if (proxyUrl.getCount() < 1)
                    urlService.deleteProxyUrl(proxyUrl);
            }
            urlService.updateProxyUrl(proxyUrl);
        } else {
            if (flag) {
                urlService.saveProxyUrl(proxyUrl);
                log.info("数据库存入一个IP{}",proxyUrl);
            }
        }
    }

    //获取当前方位的的代理IP
    public ProxyUrl getProxyUrl() {
        return proxyUrl;
    }
}
