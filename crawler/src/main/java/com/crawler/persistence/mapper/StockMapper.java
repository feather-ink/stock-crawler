package com.crawler.persistence.mapper;

import com.crawler.pojo.page.Page;
import com.crawler.pojo.stock.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StockMapper {

    //保存股票缩略信息
    void saveStock(Stock stock);

    //查询数据库中股票总数
    int queryTotalNumber();

    //查询数据库中未完成查询的股票任务总数
    int queryUnusedTotalNumber();

    //分页查询数据库中股票集合
    List<Stock> queryStockList(Page page);

    //按照一定范围查询数据库中未完成查询的股票任务集合
    List<Stock> queryUnusedStockList(int scope);
}
