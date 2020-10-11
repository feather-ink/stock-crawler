package com.crawler.parser;

import com.crawler.pojo.proxy.ProxyPool;
import com.crawler.pojo.proxy.ProxyUrl;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IpPoolParse implements ContentParser{

    //解析网页，获取IP代理
    @Override
    public void htmlParse(String content) {
        //解析content
        Document doc = Jsoup.parse(content);
        //查找标签tbody的子标签tr内容集合
        Elements tbody = doc.select("tbody > tr");
        //数据异常跳出for循环
        jump:
        for (Element element : tbody) {
            //查找td标签集合
            Elements tds = element.select("td");
            int count = 0;
            //创建代理IP对象
            ProxyUrl proxyUrl = new ProxyUrl();
            for (Element td : tds) {
                if (count == 0) {
                    //存入主机名
                    proxyUrl.setHost(td.text());
                } else if (count == 1) {
                    try{
                        //存入端口号
                        proxyUrl.setPort(Integer.parseInt(td.text()));
                    }catch (Exception e){
                        log.warn("解析数据是：{}",td.text());
                        break jump;
                    }
                } else
                    break;
                count++;
            }
            try {
                //将代理IP存入IP池
                ProxyPool.getInstance().putProxyPool(proxyUrl);
                log.info("添加一个代理IP：{}",proxyUrl);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
