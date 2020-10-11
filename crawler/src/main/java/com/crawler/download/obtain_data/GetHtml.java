package com.crawler.download.obtain_data;

import com.crawler.download.DownloadData;
import com.crawler.download.RemoteConnect;
import com.crawler.util.HtmlUtils;

import java.io.IOException;

public class GetHtml extends DownloadData {

    //本类用于获取html数据

    public GetHtml(RemoteConnect remote) {
        super(remote);
    }

    @Override
    protected String obtainData() throws IOException {
        return HtmlUtils.toString(response);
    }
}
