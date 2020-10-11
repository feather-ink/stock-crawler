package com.crawler.demo;

import com.crawler.pojo.stock.StockMessage;
import com.crawler.util.ESClient.ESClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ElasticSearchTest {

    //测试数据添加
    @Test
    public void testAddData() throws ParseException, IOException {
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

    @Test
    public void testJson(){
        File file = new File("F:\\crawler\\shell\\json.txt");
        FileReader reader = null;
        String content = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new FileReader(file, Charset.forName("utf8"));
            char[] chars = new char[1024];
            int length;
            while ((length = reader.read(chars)) != -1){

                content = String.valueOf(chars, 0, length);
                builder.append(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String jsonString = builder.toString();

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonString);
            JsonNode data = jsonNode.path("data");
            JsonNode name = data.path("code");

            System.out.println(name.asText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
