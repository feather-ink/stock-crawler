package com.crawler.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class HtmlUtils {

    //用于解决html乱码问题

    private static CharArrayBuffer buffer;

    //从请求头ContentType获取Charset字符集
    public static Charset fromContentType(HttpEntity entity) {
        ContentType contentType = ContentType.get(entity);
        Charset charset = null;
        if (contentType != null) {
            charset = contentType.getCharset();
        }
        return charset;
    }

    //从HTML中获取Charset字符集
    private static Charset fromHtml(InputStream inStream) throws IOException {
        InputStreamReader isr = new InputStreamReader(inStream, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        char[] charsByte = new char[1024];
        int l;
        String temp;
        while ((l = isr.read(charsByte)) != -1) {
            buffer.append(charsByte, 0, l);
            temp = charsByte.toString();
            temp = temp.toLowerCase();
            String regex = "charset=\"?(.+?)\"?\\s?/?>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(temp);
            if (matcher.find()) {
                String getCharset = matcher.group(1);
                return Charset.forName(getCharset);
            }
            if (temp.contains("</head>"))
                return StandardCharsets.UTF_8;
        }
        return StandardCharsets.UTF_8;
    }

    //获取Response中的html字符串
    public static String toString(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        InputStream inStream = entity.getContent();

        if (inStream == null) {
            return null;
        } else {
            try {
                Args.check(entity.getContentLength() <= 2147483647L, "HTTP entity too large to be buffered in memory");
                int capacity = (int) entity.getContentLength();
                if (capacity < 0) {
                    capacity = 4096;
                }
                buffer = new CharArrayBuffer(capacity);
                Charset charset = fromContentType(entity);
                if (charset == null)
                    charset = fromHtml(inStream);


                Reader reader = new InputStreamReader(inStream, charset);
                char[] tmp = new char[1024];
                int l;
                while ((l = reader.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                }

                String var9 = buffer.toString();
                return var9;
            } finally {
                inStream.close();
            }
        }
    }
}