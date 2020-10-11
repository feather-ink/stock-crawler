package com.crawler.persistence.mapper;

import com.crawler.pojo.page.Page;
import com.crawler.pojo.proxy.ProxyUrl;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ProxyUrlMapper {

    //查找数据库中代理IP总条数
    int queryTotalNumber();

    //查找分页查询代理IP
    List<ProxyUrl> queryProxyUrlList(Page page);

    //随机查询代理IP
    List<ProxyUrl> queryProxyUrlListByRandom();

    //查找指定代理IP
    List<ProxyUrl> findOne(ProxyUrl proxyUrl);

    //添加一个代理IP到数据库中
    void addProxyUrl(ProxyUrl proxyUrl);

    //修改数据库的代理IP
    void updateProxyUrl(ProxyUrl proxyUrl);

    //删除数据库的代理IP
    void deleteProxyUrl(ProxyUrl proxyUrl);
}
