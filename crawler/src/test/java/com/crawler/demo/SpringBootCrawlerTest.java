package com.crawler.demo;


import com.crawler.CrawlerDemo;
import com.crawler.custom_exception.IPUseUpException;
import com.crawler.download.DownloadData;
import com.crawler.download.obtain_data.GetFile;
import com.crawler.download.obtain_data.GetHtml;
import com.crawler.download.RemoteConnect;
import com.crawler.parser.SJSTimeDataParse;
import com.crawler.pojo.page.Page;
import com.crawler.pojo.proxy.ProxyUrl;
import com.crawler.pojo.stock.Stock;
import com.crawler.service.StockService;
import com.crawler.util.request_get.HttpGetBuilder;
import com.crawler.pojo.proxy.ProxyPool;
import com.crawler.pojo.url.SeedUrl;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CrawlerDemo.class)
public class SpringBootCrawlerTest {

    @Autowired
    private RemoteConnect remoteConnect;

    @Autowired
    private StockService stockService;

    @Autowired
    private SJSTimeDataParse sjsTimeDataParse;

    //IP池测试
    @Test
    public void testInit() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            ProxyUrl proxyUrl = ProxyPool.getInstance().takeProxyPool();
            System.out.println("拿走了一个" + proxyUrl);
            System.out.println(i);
            Thread.sleep(100);
        }
    }


    @Test
    public void testThread() {
        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(processors);
        for (int i = 0; i < 16; i++){
            executor.submit(() ->  {
                while (stockService.haveStock()){
                    Stock stock = stockService.getStock();
                }
            });
        }
    }


    //页面爬取测试
    @Test
    public void testOrientationCrawling() {
        DownloadData downloadData = new GetHtml(remoteConnect);
        HttpGetBuilder getBuilder = new HttpGetBuilder();
        SeedUrl seedUrl;
//        String link = "http://www.szse.cn/api/market/ssjjhq/getTimeData?random=0.48625448458278697&marketId=1&code=000001";
//        String link = "http://www.szse.cn/api/market/ssjjhq/getTimeData?random=0.12345678910111213&marketId=1&code=000002";
//        String link = "http://www.szse.cn/api/market/ssjjhq/getHistoryData?random=0.11425451123438562&cycleType=32&marketId=1&code=000001";
//        String link = "http://www.szse.cn/api/market/ssjjhq/getHistoryData?random=0.5656085621039868&cycleType=33&marketId=1&code=000001";
        String link = "http://www.szse.cn/api/market/ssjjhq/getHistoryData?random=0.5656085621039868&cycleType=33&marketId=1&code=000001";
        String referer = "http://www.szse.cn/market/trend/index.html";
        seedUrl = new SeedUrl(link, referer);
        HttpGet httpGet = getBuilder.obtainHttpGet(seedUrl);
        String content = null;
        try {
            content = downloadData.doGetData(httpGet);
        } catch (InterruptedException | IPUseUpException e) {
            e.printStackTrace();
        }

//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            JsonNode jsonNode = mapper.readTree(content);
//            jsonNode.g
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        File file = new File("F:\\crawler\\shell\\month.html");

        FileOutputStream outputStream = null;
        OutputStreamWriter writer = null;
        try {
            outputStream = new FileOutputStream(file);
            writer = new OutputStreamWriter(outputStream, "utf-8");
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //文件爬取测试
    @Test
    public void testGetFile() {
        String pathFile = "E:\\";
        DownloadData downloadData = new GetFile(remoteConnect, pathFile);
        HttpGetBuilder getBuilder = new HttpGetBuilder();
        SeedUrl seedUrl;
        String link = "http://www.szse.cn/api/report/ShowReport?SHOWTYPE=xlsx&CATALOGID=1815_stock&TABKEY=tab1&txtBeginDate=2020-09-30&txtEndDate=2020-09-30&radioClass=00%2C20%2C30&txtSite=all&random=0.6423123421375535";
        String referer = "http://www.szse.cn/market/trend/index.html";
        seedUrl = new SeedUrl(link, referer);
        HttpGet httpGet = getBuilder.obtainHttpGet(seedUrl);
        String content = null;
        try {
            content = downloadData.doGetData(httpGet);
        } catch (InterruptedException | IPUseUpException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetData() {
        DownloadData downloadData = new GetHtml(remoteConnect);
        HttpGetBuilder getBuilder = new HttpGetBuilder();
        ProxyUrl proxyUrl = new ProxyUrl();
        proxyUrl.setHost("221.180.170.104");
        proxyUrl.setPort(8080);
        downloadData.setProxyUrl(proxyUrl);
        SeedUrl seedUrl;
        String referer = "http://www.szse.cn/market/trend/index.html";
        Page page = new Page(0, 10, stockService.queryTotalNumber());
        Stock stock = new Stock();
        stock.setCode("000803");

        double random = Math.random();
        String link = "http://www.szse.cn/api/market/ssjjhq/getTimeData?random="
                + random
                + "&marketId=1&code="
                + stock.getCode();
        seedUrl = new SeedUrl(link, referer);
        HttpGet httpGet = getBuilder.obtainHttpGet(seedUrl);
        try {
            String content = downloadData.doGetData(httpGet);
            sjsTimeDataParse.setStock(stock);
            sjsTimeDataParse.htmlParse(content);
            Thread.sleep(5000);
        } catch (InterruptedException | IPUseUpException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testVisitHomepage() {
        String link = "http://www.szse.cn/market/trend/index.html";
        SeedUrl seedUrl;
        seedUrl = new SeedUrl(link);
        HttpGetBuilder getBuilder = new HttpGetBuilder();
        HttpGet httpGet = getBuilder.obtainHttpGet(seedUrl);
        List<ProxyUrl> proxyUrls = new ArrayList<>();
        while (true) {
            try {
                DownloadData downloadData = new GetHtml(remoteConnect);
                downloadData.doGetData(httpGet);
                ProxyUrl proxyUrl = downloadData.getProxyUrl();
                proxyUrls.add(proxyUrl);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IPUseUpException e) {
                e.printStackTrace();
                break;
            }
        }
        log.info("有{}个可正常使用", proxyUrls.size());
        Page page = new Page(0, 100, stockService.queryTotalNumber());
        log.info("需要查询{}次", page.getTotalNumber());

        int processors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(processors);

        for (ProxyUrl proxyUrl : proxyUrls) {
            executor.submit(() -> allocationTask(proxyUrl, link));
        }
    }

    public void allocationTask(ProxyUrl proxyUrl, String referer) {
        DownloadData downloadData = new GetHtml(remoteConnect);
        HttpGetBuilder getBuilder = new HttpGetBuilder();
        downloadData.setProxyUrl(proxyUrl);
        while (stockService.haveStock()) {
            Stock stock = stockService.getStock();
            double random = Math.random();
            String link = "http://www.szse.cn/api/market/ssjjhq/getTimeData?random="
                    + random
                    + "&marketId=1&code="
                    + stock.getCode();
            SeedUrl seedUrl = new SeedUrl(link, referer);
            HttpGet httpGet = getBuilder.obtainHttpGet(seedUrl);
            String content = null;
            try {
                content = downloadData.doGetData(httpGet);
            } catch (InterruptedException | IPUseUpException e) {
                e.printStackTrace();
            }
            SJSTimeDataParse sjsTimeDataParse = new SJSTimeDataParse();
            sjsTimeDataParse.setStock(stock);
            sjsTimeDataParse.htmlParse(content);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
