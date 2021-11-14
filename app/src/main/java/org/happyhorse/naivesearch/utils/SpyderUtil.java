package org.happyhorse.naivesearch.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SpyderUtil {
    public static String getSearchResult(int engine, String keyword, int page) throws IOException {
        String url = urlMaker(engine,keyword,page);
        Document document = request(url);
        String result = document.toString();
        return result;
    }

    public String getKeyword(){
        return null;
    }

    public static Document request(String url) throws IOException {
        Connection connect = Jsoup.connect(url);
        Document document = connect
                .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")
                .timeout(6000)
                .ignoreContentType(true)
                .get();
        return document;
    }

    public static String urlMaker(int engine,String keyword,int page) {
        List<String> url_list = Arrays.asList("https://www.baidu.com/s?&wd=",
                "https://cn.bing.com/search?q=",
                "https://www.sogou.com/web?query=");
        List<String> page_temp = Arrays.asList("&pn=","&first=","&page=");
        String page_url;
        if (engine!=2){
            page_url=page_temp.get(engine)+(page-1)*10;
        }else{
            page_url=page_temp.get(engine)+page;
        }
        return url_list.get(engine)+keyword + page_url;
    }
}
