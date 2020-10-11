package com.crawler.service.Impl;

import com.crawler.persistence.mapper.StockDataMapper;
import com.crawler.pojo.stock.StockData;
import com.crawler.service.StockDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;



@Service
public class StockDataServiceImpl implements StockDataService {

    @Autowired
    private StockDataMapper stockDataMapper;

    //保存股票数据
    @Override
    public void saveStockData(StockData stockData) {
        //如果有异常，则表明临时表未创建，创建临时表后再保存
        try{
            stockDataMapper.saveStockData(stockData);
        }catch (BadSqlGrammarException e){
            e.printStackTrace();
            stockDataMapper.createTempTable();
            this.saveStockData(stockData);
        }
    }

    //创建临时表
    @Override
    public void createTempTable() {
        stockDataMapper.createTempTable();
    }

    //将临时表中非重复数据保存的主表中
    @Override
    public void copyDataToStockData() {
        //将临时表中非重复数据保存的主表中
        stockDataMapper.insertData();
        //清空临时表的数据
        stockDataMapper.truncateTable();
    }

    //删除临时表
    @Override
    public void dropTemp() {
        stockDataMapper.dropTemp();
    }
}
