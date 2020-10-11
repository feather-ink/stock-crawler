package com.crawler.service.Impl;

import com.crawler.persistence.mapper.CalendarMapper;
import com.crawler.pojo.stock.CalendarTable;
import com.crawler.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalendarServiceImpl implements CalendarService {

    @Autowired
    private CalendarMapper calendarMapper;

    //存储日期
    @Override
    public void saveCalendar(CalendarTable calendar) {
        //查找该日期是否存在
        CalendarTable id = calendarMapper.findById(calendar.getId());
        //如果日期不存在，则保存该日期
        if (id == null){
            calendarMapper.saveCalendar(calendar);
        }
    }
}
