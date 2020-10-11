package com.crawler.parser;

import com.crawler.pojo.stock.CalendarTable;
import com.crawler.pojo.stock.Stock;
import com.crawler.pojo.stock.StockData;
import com.crawler.service.CalendarService;
import com.crawler.service.StockDataService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SJSTimeDataParse implements ContentParser {

    //深交所分时数据解析式
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private StockDataService stockDataService;

    private Stock stock;

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    //解析深交所爬取的股票数据
    @Override
    public void htmlParse(String content) {
        //创建mapper对象
        ObjectMapper mapper = new ObjectMapper();
        try {
            StockData stockData = new StockData();
            //读取content的作为json的树节点
            JsonNode jsonNode = mapper.readTree(content);

            //根据节点路径获取子节点
            JsonNode dataNode = jsonNode.path("data");
            //获取节点下"code"的内容
            String code = dataNode.get("code").asText();
            stockData.setCode(code);

            //获取节点下"datetime"的内容
            String datetime = jsonNode.get("datetime").asText();
            //清除datetime多余内容
            String date = datetime.substring(0, 10);
            stockData.setCalendar(date);
            CalendarTable calendar = new CalendarTable(date);
            //保存日期
            calendarService.saveCalendar(calendar);

            //获取其他节点内容，存入StockData对象中，保存存入StockData至数据库
            JsonNode arrNode = dataNode.get("picupdata");
            for (JsonNode node : arrNode) {
                String text = node.toString();
                String[] split = text.substring(1, text.length() - 1).replace("\"", "").split(",");
                stockData.setTime(split[0]);
                stockData.setPrice(Float.parseFloat(split[1]));
                stockData.setAverage(Float.parseFloat(split[2]));
                stockData.setChange(Float.parseFloat(split[3]));
                stockData.setRange(Float.parseFloat(split[4]));
                stockData.setExchange(Integer.parseInt(split[5]));
                stockData.setTurnover(Double.parseDouble(split[6]));
                stockDataService.saveStockData(stockData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
