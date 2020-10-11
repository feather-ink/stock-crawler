package com.crawler.service;

import com.crawler.pojo.stock.StockData;


public interface StockDataService {

    void saveStockData(StockData stockData);

    void createTempTable();

    void copyDataToStockData();

    void dropTemp();
}
