package com.crawler.service;

import com.crawler.pojo.page.Page;
import com.crawler.pojo.proxy.ProxyUrl;

public interface ProxyUrlService {

    int queryTotalNumber();

    void initProxyUrl(Page page);

    void initProxyUrlByRandom();

    void saveProxyUrl(ProxyUrl proxyUrl);

    void updateProxyUrl(ProxyUrl proxyUrl);

    void deleteProxyUrl(ProxyUrl proxyUrl);

    boolean isExistProxyUrl(ProxyUrl proxyUrl);
}
