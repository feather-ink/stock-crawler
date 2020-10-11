package com.crawler.download.obtain_data;

import com.crawler.download.DownloadData;
import com.crawler.download.RemoteConnect;
import com.crawler.pojo.url.SeedUrl;

import java.io.*;
import java.util.UUID;

public class GetImage extends DownloadData {

    //本类用于获取图片数据

    private String picName;

    private String filePath;


    public GetImage(RemoteConnect remote, String filePath, String picName) {
        super(remote);
        this.picName = picName;
        this.filePath = filePath;
    }

    public GetImage(RemoteConnect remote, String filePath, SeedUrl seedUrl){
        super(remote);
        String url = seedUrl.getLink();
        String extName = url.substring(url.lastIndexOf("."));
        picName = UUID.randomUUID().toString() + extName;
        this.filePath = filePath;
    }

    @Override
    protected String obtainData() throws IOException {
        OutputStream out = new FileOutputStream(new File(filePath + picName));
        response.getEntity().writeTo(out);
        return picName;
    }
}
