package com.crawler.pojo.page;

import lombok.Data;

@Data
public class Page {

    //起始位置
    private int current;

    //查询末位置，用作于下一页查询的起始位置
    private int bottom;

    //每次查询数据的量
    private int increment;

    //当前页数
    private int pagination;

    //总共需要查询的页数
    private int totalNumber;

    public Page(int current, int increment, int totalNumber) {
        this.current = current;
        this.increment = increment;
        this.bottom = current + increment;
        this.pagination = 1;
        this.totalNumber = totalNumber;
    }

    public boolean nextPage(){
        if (totalNumber > bottom){
            this.pagination++;
            this.current = this.bottom;
            this.bottom = this.current + this.increment;
            return true;
        }
        return false;
    }
}
