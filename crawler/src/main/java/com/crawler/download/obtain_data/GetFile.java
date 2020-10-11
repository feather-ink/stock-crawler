package com.crawler.download.obtain_data;

import com.crawler.download.DownloadData;
import com.crawler.download.RemoteConnect;
import com.crawler.util.HtmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GetFile extends DownloadData {

    private String filePath;

    public GetFile(RemoteConnect remote, String filePath) {
        super(remote);
        this.filePath = filePath;
    }

    @Override
    protected String obtainData() throws IOException {
        Header[] headers = response.getHeaders("Content-disposition");
        String docName = "无法识别文件类型";
        for (Header header : headers){
            if (header.getValue().contains("filename")){
                String disposition = new String(
                        header.getValue().getBytes(HTTP.DEF_CONTENT_CHARSET),
                        HtmlUtils.fromContentType(response.getEntity())
                );
                Pattern pattern = Pattern.compile("filename=(.+)");
                Matcher matcher = pattern.matcher(disposition);
                if (matcher.find()){
                    docName = matcher.group(1).replace("\"","");
                }
            }
        }
        if (docName == "无法识别文件类型"){
            return docName;
        }
        String[] split = docName.split("\\.");
        String fileName = split[0];
        String extName = split[1];
        File file = null;
        int i = 1;
        while (true){
            file = new File(filePath + docName);
            if (!file.exists()) {
                break;
            }
            docName = fileName + "(" + i + ")" + "." + extName;
            i++;
        }

        OutputStream out = new FileOutputStream(file);
        response.getEntity().writeTo(out);
        return docName;
    }
}
