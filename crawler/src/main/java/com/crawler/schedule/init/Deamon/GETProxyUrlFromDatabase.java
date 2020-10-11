package com.crawler.schedule.init.Deamon;

import com.crawler.pojo.page.Page;
import com.crawler.service.ProxyUrlService;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "crawler.random")
@Component
public class GETProxyUrlFromDatabase extends InitCrawler implements Runnable{

    @Autowired
    private ProxyUrlService proxyUrlService;

    private boolean initRandom;

    @Override
    public void run() {
        super.initCrawler();
        Page page = new Page(0,10, proxyUrlService.queryTotalNumber());
        boolean flag = true;
        while (flag){
            if (initRandom){
                proxyUrlService.initProxyUrlByRandom();
            }else {
                proxyUrlService.initProxyUrl(page);
                System.out.println(page.getCurrent() + ": " + page.getBottom());
                flag = page.nextPage();
            }
        }
        System.out.println("IP已经用完");
    }

    public boolean isInitRandom() {
        return initRandom;
    }

    public void setInitRandom(boolean initRandom) {
        this.initRandom = initRandom;
    }
}
