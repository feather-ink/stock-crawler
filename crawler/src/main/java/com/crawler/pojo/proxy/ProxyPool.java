package com.crawler.pojo.proxy;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProxyPool {

    private static ProxyPool instance = new ProxyPool();

    private BlockingQueue<ProxyUrl> proxyPool = new LinkedBlockingQueue<>(10);

    //禁止外界创建
    private ProxyPool (){
    }

    //获取IP池对象
    public static ProxyPool getInstance() {
        return instance;
    }

    //获取代理IP
    public ProxyUrl takeProxyPool() throws InterruptedException {
        return proxyPool.take();
    }

    //存入代理IP
    public void putProxyPool(ProxyUrl proxyUrl) throws InterruptedException {
        this.proxyPool.put(proxyUrl);
    }

    public boolean isEmpty(){
        return this.proxyPool.isEmpty();
    }

    //重置代理IP池
    public void refactorProxyPool(){
        proxyPool.clear();
    }

    public int size(){
        return proxyPool.size();
    }

    @Override
    public String toString() {
        return "ProxyPool{" +
                "proxyPool=" + proxyPool +
                '}';
    }
}
