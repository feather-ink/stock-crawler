package com.crawler.persistence.mapper;

import com.crawler.pojo.stock.StockData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface StockDataMapper {

    //保存股票信息
    void saveStockData(StockData stockData);

    //创建临时表
    void createTempTable();

    //清空临时表
    void truncateTable();

    //将临时表中非重复数据保存的主表中
    void insertData();

    //删除临时表
    void dropTemp();
}
