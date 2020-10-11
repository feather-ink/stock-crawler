package com.crawler.pojo.stock;

import com.crawler.custom_exception.FormatException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeTable {

    private String id;

    private String hour;

    private String minute;

    public TimeTable(String id) {
        this.id = id;
        this.hour = id.substring(0,2);
        this.minute = id.substring(3);
    }

    public void build() throws FormatException {
        if (this.id == null) {
            if (this.hour == null || this.minute == null) {
                throw new FormatException("hour或minute值为空");
            }
            this.id = this.hour + this.minute;
        }
        else{
            if (id.length() < 5){
                throw new FormatException("id长度小于4");
            }
            this.hour = id.substring(0,2);
            this.minute = id.substring(3);
        }
    }
}
