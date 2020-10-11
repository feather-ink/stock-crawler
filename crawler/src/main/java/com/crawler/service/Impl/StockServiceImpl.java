package com.crawler.service.Impl;

import com.crawler.persistence.mapper.StockMapper;
import com.crawler.pojo.page.Page;
import com.crawler.pojo.stock.Stock;
import com.crawler.service.StockService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockMapper stockMapper;

    private Page page;

    private List<Stock> stocks = new ArrayList<>();

    private int scope = 100;

    private boolean flag = true;

    //初始化查询股票任务
    public void queryStockInit(Page page){
        this.page = page;
        //分页查询数据
        this.stocks = this.stockMapper.queryStockList(this.page);
    }

    //保存股票缩略数据
    @Override
    public void saveStock(Stock stock) {
        stockMapper.saveStock(stock);
    }

    //查询数据库中股票总数
    @Override
    public int queryTotalNumber() {
        return stockMapper.queryTotalNumber();
    }

    //是否还有需要查询的股票任务
    @Override
    public synchronized boolean haveStock() {
        if (stocks.isEmpty()) {
            if (page.nextPage()) {
                stocks = stockMapper.queryStockList(this.page);
                return true;
            }
            return false;
        }
        return true;
    }

    //是否还有未完成查询的股票任务需要查询
    @Override
    public synchronized boolean haveUnusedStock(){
        if (stocks.isEmpty()){
            if (stockMapper.queryUnusedTotalNumber() >= scope){
                stocks = stockMapper.queryUnusedStockList(this.scope);
                return true;
            }else{
                if (flag){
                    stocks = stockMapper.queryUnusedStockList(this.scope);
                    flag = false;
                    return true;
                }else {
                    return false;
                }
            }
        }
        return true;
    }

    //获取股票任务
    @Override
    public synchronized Stock getStock() {
        return stocks.remove(0);
    }

}
