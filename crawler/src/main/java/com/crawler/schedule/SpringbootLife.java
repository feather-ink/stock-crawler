package com.crawler.schedule;

import com.crawler.schedule.init.Deamon.GETProxyUrlFromDatabase;
import com.crawler.schedule.init.Deamon.GetProxyUrlFromNetwork;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@ConfigurationProperties(prefix = "crawler.init")
@Component
public class SpringbootLife {

    @Autowired
    private GetProxyUrlFromNetwork urlFromNetwork;

    @Autowired
    private GETProxyUrlFromDatabase urlFromDatabase;

    private boolean initLocalContext;

    public boolean isInitLocalContext() {
        return initLocalContext;
    }

    public void setInitLocalContext(boolean initLocalContext) {
        this.initLocalContext = initLocalContext;
    }

    @PostConstruct
    public void initSpringboot(){
        log.info("初始化中......");
        //守护线程是从本地数据库获取IP还是从网页爬取代理IP使用
        if (initLocalContext){
            this.initLocalContext();
        }else {
            this.initNetworkContext();
        }
        log.info("完成初始化");
    }

    //初始化，采用守护线程自动爬取IP池，为爬取网页提供IP
    private void initNetworkContext() {
        //创建线程任务
        Thread getUrlThread = new Thread(urlFromNetwork);
        //设置未守护线程
        getUrlThread.setDaemon(true);
        //开启线程
        getUrlThread.start();
        //等待爬取的第一批代理IP后，完成初始化
        while (getUrlThread.getState() == Thread.State.RUNNABLE){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //初始化，采用守护线程从数据库中获取IP，为爬取网页提供IP
    private void initLocalContext(){
        //创建线程任务
        Thread getUrl = new Thread(urlFromDatabase);
        //设置守护线程
        getUrl.setDaemon(true);
        //开启线程
        getUrl.start();
    }
}
