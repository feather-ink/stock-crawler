package com.crawler.persistence.mapper;

import com.crawler.pojo.stock.TimeTable;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TimeMapper {

    //查询时间表条数
    int queryRows();

    //保存数据
    void saveTime(TimeTable timeTable);
}
