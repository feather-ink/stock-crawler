package com.crawler.schedule.init.Deamon;

import com.crawler.service.init.InitDatabases;
import org.HdrHistogram.LinearIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class InitCrawler {

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private InitDatabases initDatabases;

    protected void initCrawler(){
        initDatabases.initDatabases(this.getSchema());
    }

    private String getSchema(){
        String url = dataSourceProperties.getUrl();
        Pattern pattern = Pattern.compile("/.*/(.*)\\?");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }
}
