package com.crawler.persistence.mapper;

import com.crawler.pojo.stock.CalendarTable;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CalendarMapper {

    //保存日期
    void saveCalendar(CalendarTable calendarTable);

    //查找指定日期
    CalendarTable findById(String id);
}
