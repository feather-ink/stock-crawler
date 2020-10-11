package com.crawler.demo;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelTest {

    //写Excel
    @Test
    public void testWriteExcel(){
        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFSheet sheet = workbook.createSheet("员工信息");

        sheet.setColumnWidth(3,20*256);
        sheet.setColumnWidth(4,20*256);

        XSSFRow row = sheet.createRow(0);

        XSSFCell cell = row.createCell(0);

        cell.setCellValue("姓名");

        row.createCell(1).setCellValue("年龄");
        row.createCell(2).setCellValue("性别");
        row.createCell(3).setCellValue("生日");
        row.createCell(4).setCellValue("手机号");

        row = sheet.createRow(1);
        row.createCell(0).setCellValue("T");
        row.createCell(1).setCellValue("保密");
        row.createCell(2).setCellValue("男");
        row.createCell(3).setCellValue("保密");
        row.createCell(4).setCellValue("12121212121");

        row = sheet.createRow(2);
        row.createCell(0).setCellValue("T");
        row.createCell(1).setCellValue("18");
        row.createCell(2).setCellValue("女");
        row.createCell(3).setCellValue("2000-01-01");
        row.createCell(4).setCellValue("12121212122");

        File file = new File("F:\\员工信息.xlsx");
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            workbook.write(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //读Excel
    @Test
    public void testReadExcel(){
        File file = new File("F:\\A股列表.xlsx");
        //获得该文件的输入流
        FileInputStream stream = null;
        Workbook sheets = null;
        try {
            stream = new FileInputStream(file);
            sheets = new XSSFWorkbook(stream);

            Sheet sheet = sheets.getSheetAt(0);
            for (int i = 1; i<= sheet.getLastRowNum() ; i++) {


                // 获取行数
                Row row = sheet.getRow(i);
                // 获取单元格 取值
                for (int j = 0; j < row.getLastCellNum(); j++){
                    String cellValue = row.getCell(j).getStringCellValue();
                    if (cellValue != null){
                        System.out.print(cellValue + " ");
                    }
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (sheets != null){
                try {
                    sheets.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (stream != null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
