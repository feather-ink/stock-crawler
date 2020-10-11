package com.crawler.service.init.Impl;

import com.crawler.custom_exception.OutOfBoundsException;
import com.crawler.persistence.mapper.TimeMapper;
import com.crawler.pojo.stock.TimeTable;
import com.crawler.service.init.InitTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitTimeTableImpl implements InitTable {

    //如果时间表没有数据，则初始化时间表
    @Autowired
    private TimeMapper timeMapper;

    //初始化时间表
    @Override
    public void initTable() throws OutOfBoundsException {
        int rows = timeMapper.queryRows();
        //小于1440，补充数据
        if (rows < 1440){
            this.fillData();
            //大于1440，则该时间表数据内容异常
        }else if (rows > 1440){
            throw new OutOfBoundsException("时间表的数据超出预期，请核对！");
        }
    }

    //填充时间表数据
    private void fillData(){
        String hours;
        String minutes;
        for (int hour = 0; hour < 24; hour++) {
            if (hour < 10)
                hours = "0" + hour;
            else
                hours = "" + hour;
            for (int minute = 0; minute < 60; minute++) {
                if (minute < 10)
                    minutes = ":0" + minute;
                else {
                    minutes = ":" + minute;
                }
                TimeTable timeTable = new TimeTable(hours + minutes);
                timeMapper.saveTime(timeTable);
            }
        }
    }
}
