package com.crawler.pojo.url;

import lombok.Data;


@Data
public class SeedUrl {

    private String link;

    private String referer;

    private String cookie;

    public SeedUrl(String link) {
        this.link = link;
    }

    public SeedUrl(String link, String referer) {
        this.link = link;
        this.referer = referer;
    }
}
