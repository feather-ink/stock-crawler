package com.crawler.pojo.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockData {

    //股票代码
    private String code;

    //日期
    private String calendar;

    //时间
    private String time;

    //最新价
    private float price;

    //均价
    private float average;

    //涨幅
    private float change;

    //涨跌幅（%）
    private float range;

    //成交量（手）
    private int exchange;

    //成交额
    private double turnover;
}
