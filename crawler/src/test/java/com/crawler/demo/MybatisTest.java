package com.crawler.demo;


import com.crawler.CrawlerDemo;
import com.crawler.persistence.mapper.StockDataMapper;
import com.crawler.persistence.mapper.init.DatabasesMapper;
import com.crawler.persistence.mapper.TimeMapper;
import com.crawler.pojo.stock.CalendarTable;
import com.crawler.pojo.stock.Stock;
import com.crawler.pojo.stock.StockData;
import com.crawler.pojo.stock.TimeTable;
import com.crawler.service.CalendarService;
import com.crawler.service.StockDataService;
import com.crawler.service.StockService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CrawlerDemo.class)
public class MybatisTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private TimeMapper timeMapper;

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private StockDataService stockDataService;

    @Autowired
    private DatabasesMapper databasesMapper;



    @Test
    public void testAddData() throws ParseException, IOException {

        File file = new File("F:\\A股列表.xlsx");

        FileInputStream stream = null;
        Workbook sheets = null;

        try {
            stream = new FileInputStream(file);
            sheets = new XSSFWorkbook(stream);

            Sheet sheet = sheets.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++){
                Row row = sheet.getRow(i);

                Stock stock = new Stock(row.getCell(3).getStringCellValue(), row.getCell(4).getStringCellValue());

                stockService.saveStock(stock);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //初始化时间表
    @Test
    public void testSaveCalendar(){
        String hours;
        String minutes;
        for (int hour = 0; hour < 24; hour++) {
            if (hour < 10)
                hours = "0" + hour;
            else
                hours = "" + hour;
            for (int minute = 0; minute < 60; minute++) {
                if (minute < 10)
                    minutes = ":0" + minute;
                else {
                    minutes = ":" + minute;
                }
                TimeTable timeTable = new TimeTable(hours + minutes);
                timeMapper.saveTime(timeTable);
            }
        }
    }

    @Test
    public void testJsonObject() {
        File file = new File("F:\\crawler\\shell\\json.txt");
        ObjectMapper mapper = new ObjectMapper();

        try {
            StockData stockData = new StockData();
            JsonNode jsonNode = mapper.readTree(file);

            JsonNode dataNode = jsonNode.path("data");
            String code = dataNode.get("code").asText();
            stockData.setCode(code);

            String datetime = jsonNode.get("datetime").asText();
            String date = datetime.substring(0, 10);
            stockData.setCalendar(date);
            CalendarTable calendar = new CalendarTable(date);
            calendarService.saveCalendar(calendar);

            JsonNode arrNode = dataNode.get("picupdata");
            for (JsonNode node : arrNode){
                String text = node.toString();
                String[] split = text.substring(1, text.length() - 1).replace("\"","").split(",");
                stockData.setTime(split[0]);
                stockData.setPrice(Float.parseFloat(split[1]));
                stockData.setAverage(Float.parseFloat(split[2]));
                stockData.setChange(Float.parseFloat(split[3]));
                stockData.setRange(Float.parseFloat(split[4]));
                stockData.setExchange(Integer.parseInt(split[5]));
                stockData.setTurnover(Double.parseDouble(split[6]));
                System.out.println(stockData);
//                System.out.println(stockData);
                stockDataService.saveStockData(stockData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTimeTable(){
        List<String> strings = databasesMapper.traversalDatabases("crawler");
        for (String s : strings)
            System.out.println(s);
    }

    //DataIntegrityViolationException
    @Test
    public void testRemoveDuplication(){
        StockData stockData = new StockData(
                "001896",
                "2020-10-09",
                "09:31",
                2.41f,
                2.41f,
                0.01f,
                0.41f,
                83,
                20086

        );
//        StockData stockData = new StockData();
        stockDataService.saveStockData(stockData);
        stockDataService.copyDataToStockData();
    }
}
