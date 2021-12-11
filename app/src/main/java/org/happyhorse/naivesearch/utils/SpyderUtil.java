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

/**
 * This class support to get a unprocessed request results with the {@link SpyderUtil#getSearchResult(int, java.lang.String, int)} API.
 * The results are requesting with particular cookies and User Agent
 */
public class SpyderUtil {
    static boolean init = false;    //is need to initial

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
        if (!init) {
            init();
            init = true;
        }

        //choose what cookies will be attached based on given search engine
        if (engine == Config.BAIDU_SEARCH) {
            cookies = baidu_cookie; //cookies for Baidu engine
        } else if (engine == Config.BING_SEARCH) {
            cookies = bing_cookie;  //cookies for Bing engine
        }

        //build a particular URL for request
        String url = urlMaker(engine, keyword, page);

        //get the request results and return
        Document document = request(url);
        return document.toString();
    }

    /**
     * Initialize all cookies for supported search engines using {@link Jsoup#connect(java.lang.String)}
     *
     * @throws IOException come from {@link Connection#execute()}
     */
    private static void init() throws IOException {
        //get cookies for Baidu engine
        Connection conn = Jsoup.connect(Config.SEARCH_ENGINE_URL[Config.BAIDU_SEARCH]);
        Connection.Response res = conn.method(Connection.Method.POST).timeout(3000).execute();
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
    private static Document request(String url) throws IOException {
        //User Agent pool
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

        //get a User Agent from pool
        Random rand = new Random();
        //String ua = ualist.get(rand.nextInt(ualist.size()));//TODO:not used
        String ua = ualist.get(2);

        //request the url with selected User Agent and cookies, then return the request results
        Connection connect = Jsoup.connect(url);
        Document document = connect
                .userAgent(ua)
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
