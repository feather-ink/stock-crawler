package com.crawler.pojo.url;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SeedPool{

    private static SeedPool instance = new SeedPool();

    private BlockingQueue<SeedUrl> seedUrls = new LinkedBlockingQueue<>(100);

    private SeedPool(){

    }

    public static SeedPool getInstance() {
        return instance;
    }

    public SeedUrl takeProxyPool() throws InterruptedException {
        return seedUrls.take();
    }

    public void putProxyPool(SeedUrl seedUrl) throws InterruptedException {
        this.seedUrls.put(seedUrl);
    }
}
