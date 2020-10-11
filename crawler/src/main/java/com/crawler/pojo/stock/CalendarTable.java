package com.crawler.pojo.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarTable {

    private String id;

    private String year;

    private String month;

    private String day;

    public CalendarTable(String id) {
        this.id = id;
        this.year = id.substring(0,4);
        this.month = id.substring(5,7);
        this.day = id.substring(8);
    }

    public CalendarTable(String year, String month, String day) {
        this.id = year + month + day;
        this.year = year;
        this.month = month;
        this.day = day;
    }


    public void build(){
        this.id = this.year + this.month + this.day;
    }
}
