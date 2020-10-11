package com.crawler.pojo.proxy;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProxyUrl {

    private int id;

    private String host;

    private Integer port;

    private int count = 5;
}
