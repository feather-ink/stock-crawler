package com.crawler.service;

import com.crawler.pojo.page.Page;
import com.crawler.pojo.stock.Stock;


public interface StockService {

    void queryStockInit(Page page);

    void saveStock(Stock stock);

    int queryTotalNumber();

    boolean haveStock();

    boolean haveUnusedStock();

    Stock getStock();

}
