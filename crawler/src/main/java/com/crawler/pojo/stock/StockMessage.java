package com.crawler.pojo.stock;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class StockMessage {

    @JsonIgnore
    private String id;

    private String board;

    private String name;

    private String address;

    private String code;

    private String abbreviation;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date established;

    private Long totalCapital;

    private Long circulateCapital;

    private String area;

    private String province;

    private String city;

    private String industry;

    private String website;
}
