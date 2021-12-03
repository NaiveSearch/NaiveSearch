package org.happyhorse.naivesearch.utils;

import org.happyhorse.naivesearch.config.Config;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SpyderUtil {
    static boolean init = false;
    static Map<String, String> cookies, baidu_cookie, bing_cookie;

    public static String getSearchResult(int engine, String keyword, int page) throws IOException {
        if(!init){
            init();
            init=true;
        }
        if(engine== Config.BAIDU_SEARCH){
            cookies = baidu_cookie;
        }else if(engine==Config.BING_SEARCH){
            cookies = bing_cookie;
        }
        String url = urlMaker(engine,keyword,page);
        Document document = request(url);
        return document.toString();
    }

    public static void init() throws IOException {
        Connection conn = Jsoup.connect(Config.SEARCH_ENGINE_URL[Config.BAIDU_SEARCH]);
        Connection.Response res = conn.method(Connection.Method.POST).timeout(3000).execute();
        baidu_cookie = res.cookies();

        Connection conn2 = Jsoup.connect(Config.SEARCH_ENGINE_URL[Config.BING_SEARCH]);
        Connection.Response res2 = conn2.method(Connection.Method.POST).timeout(3000).execute();
        bing_cookie = res2.cookies();
    }

    public static Document request(String url) throws IOException {
        List<String> ualist = Arrays.asList(
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/22.0.1207.1 Safari/537.1 BingPreview/1.0b",
                "Mozilla/5.0 (X11; CrOS i686 2268.111.0) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.57 Safari/536.11 BingPreview/1.0b",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6 BingPreview/1.0b",
                "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1090.0 Safari/536.6 BingPreview/1.0b",
                "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/19.77.34.5 Safari/537.1 BingPreview/1.0b",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.9 Safari/536.5 BingPreview/1.0b",
                "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.36 Safari/536.5 BingPreview/1.0b",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3 BingPreview/1.0b",
                "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3 BingPreview/1.0b",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_0) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3 BingPreview/1.0b",
                "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3 BingPreview/1.0b",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3 BingPreview/1.0b",
                "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3 BingPreview/1.0b",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3 BingPreview/1.0b",
                "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3 BingPreview/1.0b",
                "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.0 Safari/536.3 BingPreview/1.0b",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24 BingPreview/1.0b",
                "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24 BingPreview/1.0b");
        Random rand = new Random();
        //String ua = ualist.get(rand.nextInt(ualist.size()));
        String ua = ualist.get(2);
        Connection connect = Jsoup.connect(url);
        Document document = connect
                .userAgent(ua)
                .timeout(3000)
                .ignoreContentType(true)
                .cookies(cookies)
                .get();
        return document;
    }


    public static String urlMaker(int engine,String keyword,int page) throws UnsupportedEncodingException {
        List<String> url_list = Arrays.asList("https://www.baidu.com/s?&wd=",
                "https://cn.bing.com/search?q=",
                "https://www.sogou.com/web?query=");
        List<String> page_temp = Arrays.asList("&pn=","&first=","&page=");
        keyword = URLEncoder.encode(keyword,"UTF-8");
        String page_url;
        if (engine!=2){
            page_url=page_temp.get(engine)+(page)*10;
        }else{
            page_url=page_temp.get(engine)+(page+1);
        }
        return url_list.get(engine)+keyword + page_url;
    }
}
