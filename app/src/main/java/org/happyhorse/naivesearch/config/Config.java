package org.happyhorse.naivesearch.config;

import java.util.Locale;

/**
 *Define the identifier and base URL for every supported search engine
 */
public class Config {
    public static final int BAIDU_SEARCH = 0;   //Baidu search engine
    public static final int BING_SEARCH = 1;    //Bing search engine
    public static final String[] SEARCH_ENGINE_URL = {"https://www.baidu.com", "https://cn.bing.com"};
    public static final int ENGLISH = 0;
    public static final int CHINESE = 1;
    public static final Locale[] LOCALES = {Locale.ENGLISH,Locale.CHINESE};

}