package com.crawler.schedule;

import com.crawler.custom_exception.IPUseUpException;
import com.crawler.download.DownloadData;
import com.crawler.download.RemoteConnect;
import com.crawler.download.obtain_data.GetHtml;
import com.crawler.parser.ContentParser;
import com.crawler.pojo.page.Page;
import com.crawler.pojo.stock.Stock;
import com.crawler.pojo.stock.StockMessage;
import com.crawler.pojo.url.SeedUrl;
import com.crawler.service.StockDataService;
import com.crawler.service.StockService;
import com.crawler.util.ESClient.ESClient;
import com.crawler.util.request_get.HttpGetBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class GetStockData {

    @Resource(name = "SJSTimeDataParse")
    private ContentParser sjsTimeDataParse;

    @Autowired
    private RemoteConnect remoteConnect;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockDataService stockDataService;

    private HttpGetBuilder builderHttpGet = new HttpGetBuilder();



    //获取股票数据,每周一至周五晚八点执行
    @Scheduled(cron = "0 0 20 ? * 2,3,4,5,6")
    public void visitStockData() throws InterruptedException {
        //创建存储数据的临时表
        stockDataService.createTempTable();
        //分页设置
        Page page = new Page(0, 100, stockService.queryTotalNumber());
        //初始化stockService，设置page
        stockService.queryStockInit(page);
        //获取系统可用线程数
        int processors = Runtime.getRuntime().availableProcessors();
        //设置线程池大小
        ExecutorService executor = Executors.newFixedThreadPool(processors);
        //提交爬取任务
        for (int i = 0; i < 16; i++) {
            executor.submit(() -> allocationTask());
        }
        //等待任务结束
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
        //爬取遗漏任务
        executor = Executors.newFixedThreadPool(processors);
        for (int i = 0; i < 16; i++) {
            executor.submit(() -> allocationUnusedTask());
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
        System.out.println("所有数据已经爬取完成");
        //去除临时表的重复数据保存至主表，清空临时表
        stockDataService.copyDataToStockData();
    }

    //用于爬取过程中程序中断后，再从上一次位置开始爬数据
//    @Scheduled(fixedDelay = 1000*1000)
    public void visitUnusedStockData(){
        //获取系统可用线程数
        int processors = Runtime.getRuntime().availableProcessors();
        //设置线程池大小
        ExecutorService executor = Executors.newFixedThreadPool(processors);
        //提交爬取任务
        for (int i = 0; i < 16; i++) {
            executor.submit(() -> allocationUnusedTask());
        }
        executor.shutdown();
        System.out.println("所有数据已经爬取完成");
    }

    //爬取任务分配
    public void allocationTask() {
        //创建多例对象
        DownloadData downloadData = new GetHtml(remoteConnect);
        //判断爬取任务是否已经完成
        while (stockService.haveStock()) {
            //执行爬取任务
            visitWebGetData(downloadData);
        }
    }

    //爬取任务分配，用于爬取未完成的任务（由于某种原因遗漏所致）
    public void allocationUnusedTask() {
        //创建多例对象
        DownloadData downloadData = new GetHtml(remoteConnect);
        //判断爬取任务是否已经完成
        while (stockService.haveUnusedStock()) {
            //执行爬取任务
            visitWebGetData(downloadData);
        }
    }

    //访问链接爬取数据
    private void visitWebGetData(DownloadData downloadData){
        //设置referer
        String referer = "http://www.szse.cn/market/trend/index.html";
        //获取爬取任务
        Stock stock = this.stockService.getStock();
        if (stock != null) {
            //链接需要
            double random = Math.random();
            //构造链接
            String link = "http://www.szse.cn/api/market/ssjjhq/getTimeData?random="
                    + random
                    + "&marketId=1&code="
                    + stock.getCode();
            //封装对象SeedUrl
            SeedUrl seedUrl = new SeedUrl(link, referer);
            //封装对象HttpGet
            HttpGet httpGet = this.builderHttpGet.obtainHttpGet(seedUrl);
            String content = null;
            try {
                //获取数据
                content = downloadData.doGetData(httpGet);
            } catch (InterruptedException | IPUseUpException e) {
                e.printStackTrace();
            }
            //解析数据
            this.sjsTimeDataParse.htmlParse(content);
            try {
                //爬取暂停
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

//    @Scheduled(cron = "0 0 20 1W * ?")
    public void saveMessageToElasticsearch() throws ParseException, IOException {
        ArrayList<StockMessage> stockMessages = new ArrayList<>();

        File file = new File("F:\\A股列表.xlsx");

        FileInputStream stream = null;
        Workbook sheets = null;

        try {
            stream = new FileInputStream(file);
            sheets = new XSSFWorkbook(stream);

            Sheet sheet = sheets.getSheetAt(0);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 1; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);

                Date established = simpleDateFormat.parse(row.getCell(5).getStringCellValue());
                String totalCapital = row.getCell(6).getStringCellValue().replace(",","");
                String circulateCapital = row.getCell(7).getStringCellValue().replace(",","");
                String value = row.getCell(11).getStringCellValue();
                String trim = value.substring(1).trim();

                StockMessage stockMessage = new StockMessage(
                        i + "",
                        row.getCell(0).getStringCellValue(),
                        row.getCell(1).getStringCellValue(),
                        row.getCell(2).getStringCellValue(),
                        row.getCell(3).getStringCellValue(),
                        row.getCell(4).getStringCellValue(),
                        established,
                        Long.parseLong(totalCapital),
                        Long.parseLong(circulateCapital),
                        row.getCell(8).getStringCellValue(),
                        row.getCell(9).getStringCellValue(),
                        row.getCell(10).getStringCellValue(),
                        trim,
                        row.getCell(12).getStringCellValue()
                );
                stockMessages.add(stockMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        RestHighLevelClient client = ESClient.getClient();
        ObjectMapper mapper = new ObjectMapper();
        String index = "stockmessage";

        BulkRequest request = new BulkRequest();
        for (StockMessage stockMessage : stockMessages){
            String message = mapper.writeValueAsString(stockMessage);
            request.add(new IndexRequest(index).id(stockMessage.getId()).source(message, XContentType.JSON));
        }
        client.bulk(request, RequestOptions.DEFAULT);
        System.out.println("OK!!");
    }
}
