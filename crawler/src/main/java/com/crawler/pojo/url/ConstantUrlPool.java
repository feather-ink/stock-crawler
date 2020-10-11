package com.crawler.pojo.url;

import java.util.ArrayList;

public class ConstantUrlPool{

    ArrayList<SeedUrl> urls = new ArrayList<>();


    public SeedUrl takeProxyPool() {
        return urls.remove(0);
    }

    public void putProxyPool(SeedUrl seedUrl) {
        urls.add(seedUrl);
    }

    public SeedUrl takeProxyPool(int index) {
        return urls.get(index);
    }
}
