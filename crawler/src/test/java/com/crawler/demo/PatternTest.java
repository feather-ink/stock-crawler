package com.crawler.demo;

import org.apache.http.util.Args;
import org.junit.jupiter.api.Test;


import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {

    @Test
    public void testPattern(){
        String sample = "attachment;filename=A股列表.xlsx";
        Pattern pattern = Pattern.compile("filename=(.+\")");
        Matcher matcher = pattern.matcher(sample);
        if (matcher.find()){
            System.out.println(matcher.group(1).replace("\"",""));
        }else {
            System.out.println("没有找到");
        }
    }

    @Test
    public void testCharset(){
        String type = ((String) Args.notBlank("mimeType", "MIME type")).toLowerCase(Locale.ROOT);
        System.out.println(type);
    }
}
