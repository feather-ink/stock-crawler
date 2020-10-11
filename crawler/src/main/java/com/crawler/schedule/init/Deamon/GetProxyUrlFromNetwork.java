package com.crawler.schedule.init.Deamon;

import com.crawler.custom_exception.IPUseUpException;
import com.crawler.download.DownloadData;
import com.crawler.download.obtain_data.GetHtml;
import com.crawler.download.RemoteConnect;
import com.crawler.util.request_get.HttpGetBuilder;
import com.crawler.parser.IpPoolParse;
import com.crawler.pojo.proxy.ProxyPool;
import com.crawler.pojo.url.SeedUrl;
import com.crawler.service.ProxyUrlService;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "crawler.proxyweb")
@Component
public class GetProxyUrlFromNetwork extends InitCrawler implements Runnable{

    @Autowired
    private ProxyUrlService proxyUrlService;

    @Autowired
    private IpPoolParse ipPoolParse;

    @Autowired
    private RemoteConnect remoteConnect;

    private String link;

    private String referer;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    @Override
    public void run() {
        //初始化线程池
        proxyUrlService.initProxyUrlByRandom();
        System.out.println(ProxyPool.getInstance());
        DownloadData downloadData = new GetHtml(remoteConnect);
        HttpGetBuilder getBuilder = new HttpGetBuilder();
        SeedUrl seedUrl;
        String link;
        //是否需要清空IP池，第一次访问时需要
        boolean flag = true;
        //初始页数
        int start = 1;
        //结束页数
        int end = 10;
        while (true){
            //输入网址
            link = this.link + start;
            seedUrl = new SeedUrl(link,referer);
            referer = link;
            //获取游览器
            HttpGet httpGet = getBuilder.obtainHttpGet(seedUrl);
            String content = null;
            try {
                content = downloadData.doGetData(httpGet);
            } catch (InterruptedException | IPUseUpException e) {
                e.printStackTrace();
            }
            if (flag){
                ProxyPool.getInstance().refactorProxyPool();
            }
            ipPoolParse.htmlParse(content);
            System.out.println(ProxyPool.getInstance());
            start++;
            if (start > end){
                start = 1;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (flag){
                super.initCrawler();
                flag = false;
            }
        }
    }
}
