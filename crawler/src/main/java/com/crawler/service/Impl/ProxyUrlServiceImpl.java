package com.crawler.service.Impl;

import com.crawler.persistence.mapper.ProxyUrlMapper;
import com.crawler.pojo.page.Page;
import com.crawler.pojo.proxy.ProxyPool;
import com.crawler.pojo.proxy.ProxyUrl;
import com.crawler.service.ProxyUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ProxyUrlServiceImpl implements ProxyUrlService {

    @Autowired
    private ProxyUrlMapper urlMapper;

    //查询数据库中代理IP的总数
    @Override
    public int queryTotalNumber() {
        return urlMapper.queryTotalNumber();
    }

    //初始化代理IP池，给IP池存入有序代理IP
    @Override
    public void initProxyUrl(Page page) {
        //查询数据库获取代理IP集合
        List<ProxyUrl> proxyUrls = urlMapper.queryProxyUrlList(page);
        //获取IP池对象
        ProxyPool proxyPool = ProxyPool.getInstance();
        //将代理IP集合中的代理IP存入IP池
        for (ProxyUrl proxyUrl : proxyUrls){
            try {
                proxyPool.putProxyPool(proxyUrl);
                log.info("从数据库获取代理IP{}，存入代理池",proxyUrl);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //初始化代理IP池，给IP池存入随机代理IP
    @Override
    public void initProxyUrlByRandom() {
        //采用随机方式查询数据库获取代理IP集合
        List<ProxyUrl> proxyUrls = urlMapper.queryProxyUrlListByRandom();
        //获取IP池对象
        ProxyPool proxyPool = ProxyPool.getInstance();
        //将代理IP集合中的代理IP存入IP池
        for (ProxyUrl proxyUrl : proxyUrls){
            try {
                proxyPool.putProxyPool(proxyUrl);
                log.info("从数据库获取代理IP{}，存入代理池",proxyUrl);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //保存代理IP
    @Override
    public void saveProxyUrl(ProxyUrl proxyUrl) {
        urlMapper.addProxyUrl(proxyUrl);
    }

    //修改代理IP
    @Override
    public void updateProxyUrl(ProxyUrl proxyUrl) {
        urlMapper.updateProxyUrl(proxyUrl);
    }

    //删除代理IP
    @Override
    public void deleteProxyUrl(ProxyUrl proxyUrl) {
        urlMapper.deleteProxyUrl(proxyUrl);
    }

    //判断代理IP是否存在，如有重复，则删除重复
    @Override
    public boolean isExistProxyUrl(ProxyUrl proxyUrl) {
        //查询该代理IP是否存在
        List<ProxyUrl> urls = urlMapper.findOne(proxyUrl);
        //不存在，返回false
        if (urls == null || urls.isEmpty()){
            return false;
        }
        else{
            //根据urls大小判断代理IP是否重复
            if (urls.size() > 1){
                //保留第一个代理IP，将其余重复的代理IP删除
                urls.remove(0);
                for (ProxyUrl url : urls) {
                    urlMapper.deleteProxyUrl(url);
                }
            }
            return true;
        }
    }
}
