package com.crawler.download;


import com.crawler.custom_exception.IPUseUpException;
import com.crawler.pojo.proxy.ProxyUrl;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ConnectException;

@Slf4j
@Component
public abstract class DownloadData {

    protected CloseableHttpResponse response;

    private RemoteConnect remote;

    public ProxyUrl getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(ProxyUrl proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

    private ProxyUrl proxyUrl;

    public DownloadData(RemoteConnect remote) {
        this.remote = remote;
    }

    //从response中获取什么样的数据，由子类实现
    protected abstract String obtainData() throws IOException;

    //获取数据
    public String doGetData(HttpRequestBase httpRequest) throws InterruptedException, IPUseUpException {

        String content = "无响应实体";

        //访问链接获取数据
        this.getResponse(httpRequest);
        log.info("当前使用的代理为：http://{}:{}", proxyUrl.getHost(), proxyUrl.getPort());

        //如果接受到数据
        if (response != null) {
            if (this.response.getEntity() != null) {
                try {
                    //由子类决定如何获取该数据
                    content = obtainData();
                } catch (IOException e) {
                    //出现异常，清空代理代理IP重新访问链接获取数据
                    //此处异常一半由代理IP返回的网页不正确，导致解析异常所致
                    this.proxyUrl = null;
                    content = doGetData(httpRequest);
                }
            }

            try {
                this.response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return content;
    }

    //判断该对象的httpclient是否有可用proxyUrl代理对象，如果没有则从代理池里拿一个
    private void getResponse(HttpRequestBase httpRequest) throws IPUseUpException, InterruptedException {
            if (proxyUrl != null) {
                try {
                    //如果代理IP不为null，则采用现有IP访问
                    log.debug("当前使用的代理为：http://{}:{}", proxyUrl.getHost(), proxyUrl.getPort());
                    this.response = remote.connectRemote(httpRequest, proxyUrl);
                } catch (ConnectException e) {
                    //出现异常则更换代理IP访问
                    this.response = remote.connectRemote(httpRequest);
                    //获取代理IP设置为本对象的代理IP
                    this.proxyUrl = remote.getProxyUrl();
                    log.warn("连接失败,更换代理为：http://{}:{}\n", proxyUrl.getHost(), proxyUrl.getPort(), e);
                }
            } else {
                //如果代理IP为null，则从IP池中获取代理IP访问
                this.response = remote.connectRemote(httpRequest);
                //获取代理IP设置为本对象的代理IP
                this.proxyUrl = remote.getProxyUrl();
                log.debug("该对象属性获取一个：{}", proxyUrl);
            }
    }
}
