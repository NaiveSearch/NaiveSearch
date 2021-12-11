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

/**
 * This class support to get a unprocessed request results with the {@link SpyderUtil#getSearchResult(int, java.lang.String, int)} API.
 * The results are requesting with particular cookies and User Agent
 */
public class SpyderUtil {
    //is need to initial
    static boolean init = false;

    //store the cookies of supported search engines
    static Map<String, String> cookies, baidu_cookie, bing_cookie;


    /**
     * Return the html stream according to given search engine, key word and page number
     * Do the initialization if not done before by calling {@link SpyderUtil#init}
     *
     * @param engine  search engine to be use
     * @param keyword key word for searching
     * @param page    page number to request
     * @return html stream
     * @throws IOException error might come from {@link SpyderUtil#init}, {@link SpyderUtil#urlMaker(int, java.lang.String, int)}, {@link SpyderUtil#request(java.lang.String)}
     */
    public static String getSearchResult(int engine, String keyword, int page) throws IOException {
        //do initialization if not initial before
        if(!init){
            init();
            init=true;
        }

        //build a particular URL for request
        String url = urlMaker(engine,keyword,page);
        Document document = null;
        //choose what header will be attached based on given search engine
        if (engine == Config.BAIDU_SEARCH) {
            cookies = baidu_cookie; //cookies for Baidu engine
            document = requestBaidu(url);
        } else if (engine == Config.BING_SEARCH) {
            cookies = bing_cookie;  //cookies for Bing engine
            document = requestBing(url);
        }

        return document.toString();
    }


    /**
     * Initialize all cookies for supported search engines using {@link Jsoup#connect(java.lang.String)}
     *
     * @throws IOException come from {@link Connection#execute()}
     */
    public static void init() throws IOException {
        //get cookies for Baidu engine
        Connection conn = Jsoup.connect(Config.SEARCH_ENGINE_URL[Config.BAIDU_SEARCH]);
        Connection.Response res = conn.method(Connection.Method.POST).timeout(3000)
                .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Encoding","gzip, deflate, br")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36")
                .execute();
        baidu_cookie = res.cookies();

        //get cookies for Bing engine
        Connection conn2 = Jsoup.connect(Config.SEARCH_ENGINE_URL[Config.BING_SEARCH]);
        Connection.Response res2 = conn2.method(Connection.Method.POST).timeout(3000).execute();
        bing_cookie = res2.cookies();
    }


    /**
     * Return the results of request the url
     * The connection will attach particular User Agent and cookies to avoid search engine`s inspection
     * User Agent will be selected from the pool
     *
     * @param url URL need to request
     * @return the requested results
     * @throws IOException come from {@link Connection#get()}
     * @see Jsoup
     */
    public static Document requestBaidu(String url) throws IOException {
        Connection connect = Jsoup.connect(url);

        Document document = connect
                .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Encoding","gzip, deflate, br")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36")
                .header("Referer","https://www.baidu.com/")
                .timeout(3000)
                .ignoreContentType(true)
                .cookies(cookies)
                .get();
        return document;
    }

    public static Document requestBing(String url) throws  IOException{
        Connection connect = Jsoup.connect(url);
        Document document = connect
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.93 Safari/537.36")
                .timeout(3000)
                .ignoreContentType(true)
                .cookies(cookies)
                .get();
        return document;
    }


    /**
     * Return a completed search URL based on search engines, key word and page number
     * key word encoded by using {@link URLEncoder#encode(java.lang.String, java.lang.String)}, and default character encoding is 'UTF-8'
     *
     * @param engine  search engine to used
     * @param keyword key word for searching
     * @param page    page number of user want to go
     * @return a completed request URL
     * @throws UnsupportedEncodingException come from {@link URLEncoder#encode(java.lang.String, java.lang.String)}
     */
    private static String urlMaker(int engine, String keyword, int page) throws UnsupportedEncodingException {

        //a list contains all the request base url for each supported search engine
        List<String> url_list = Arrays.asList("https://www.baidu.com/s?&wd=",
                "https://cn.bing.com/search?q=",
                "https://www.sogou.com/web?query=");

        //the search parameters of engine`s API
        List<String> page_temp = Arrays.asList("&pn=", "&first=", "&page=");

        //encode the key word
        keyword = URLEncoder.encode(keyword, "UTF-8");

        //assign the page number according to different form of search engine
        String page_url;
        if (engine != 2) {
            page_url = page_temp.get(engine) + (page) * 10;
        } else {
            page_url = page_temp.get(engine) + (page + 1);
        }
        return url_list.get(engine) + keyword + page_url;
    }
}
