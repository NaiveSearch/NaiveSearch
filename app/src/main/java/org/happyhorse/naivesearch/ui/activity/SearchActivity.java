package org.happyhorse.naivesearch.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.happyhorse.naivesearch.R;
import org.happyhorse.naivesearch.config.Config;
import org.happyhorse.naivesearch.databinding.ActivitySearchBinding;
import org.happyhorse.naivesearch.utils.SpyderUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * A {@link AppCompatActivity} subclass.
 * This activity to show the search results in a WebView, and support searching, page changing and go home function
 * The contents in WebView is processed by injections of JS and CSS schemes
 */
public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;

    private String keyword; //the key word will be search

    private int engine; //the search engine will be use

    private int page;   //the page number of search results

    private String content; //TODO:not used

    //key word setting API
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    //search engine setting API
    public void setEngine(int engine) {
        this.engine = engine;
    }

    //page setting API
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * Search query and show the results in a WebView
     *
     * @param webView the web view to display search results
     * @param engine  search engine
     * @param keyword key word to search
     * @param page    number of result page
     * @see SpyderUtil#getSearchResult(int, java.lang.String, int)
     */
    public void setContent(WebView webView, int engine, String keyword, int page) {
        //a Handler to deal with search results
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String unencodedHtml = bundle.getString("content");
                //String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(), Base64.NO_PADDING);
                //webView.loadData(encodedHtml, "text/html", "base64");
                webView.getSettings().setBlockNetworkImage(true);
                webView.loadDataWithBaseURL(Config.SEARCH_ENGINE_URL[engine], unencodedHtml, "text/html", "UTF-8", null);

                webView.setWebViewClient(new WebViewClient() {

                    /**
                     * Super method and process the contents in WebView with JS and CSS schemes
                     * @param view  the WebView target need to processed
                     * @param url   used to call super method
                     * @see SearchActivity#injectJS(android.webkit.WebView, int)
                     * @see org.happyhorse.naivesearch.ui.activity.SearchActivity#injectCSS(android.webkit.WebView, int)
                     *
                     */
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        injectJS(webView, engine);
                        injectCSS(webView, engine);
                        webView.getSettings().setBlockNetworkImage(false);
                    }

                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (url.startsWith("naivesearch")) {
                            getParas(url);
                        }
                        //jump to the result page
                        else {
                            Intent intent = new Intent(SearchActivity.this, ResultActivity.class);
                            Bundle http_link_bundle = new Bundle();
                            http_link_bundle.putString("link", url);
                            intent.putExtras(http_link_bundle);
                            startActivity(intent);
                        }
                        return true;
                    }

                });
            }
        };

        //start searching query and send result message
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String res = SpyderUtil.
                            getSearchResult(engine, keyword, page);
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("content", res.trim());
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    /**
     * Get parameters from given url and update it into preferences
     *
     * @param url a url contains scheme, action and query
     * @see SearchActivity#updatePreferences(int)
     */
    private void getParas(String url) {
        Uri uriRequest = Uri.parse(url);
        String scheme = uriRequest.getScheme();
        String action = uriRequest.getHost();
        String query = uriRequest.getQuery();
        if (url.startsWith("naivesearch")) {
            if ("naivesearch".equals(scheme)) {
                if (!TextUtils.isEmpty(query)) {
                    //HashMap maps = new HashMap();
                    Set<String> names = uriRequest.getQueryParameterNames();
                    for (String name : names) {
                        //maps.put(name, uriRequest.getQueryParameter(name));
                        //System.out.println(name + " " + uriRequest.getQueryParameter(name));
                        updatePreferences(Integer.parseInt(uriRequest.getQueryParameter(name)));
                    }
                }
            }
        }
    }

    /**
     * Add the advertisements number and search times into statistic preferences
     *
     * @param ads number of advertisements
     */
    private void updatePreferences(int ads) {
        SharedPreferences prefs = getSharedPreferences("statistic", MODE_PRIVATE);
        SharedPreferences.Editor prefs_editor = prefs.edit();

        //add blocked advertisements by parameter ads
        prefs_editor.putInt("blockedAD", prefs.getInt("blockedAD", 0) + ads);

        //add search time by 1
        prefs_editor.putInt("searchTime", prefs.getInt("searchTime", 0) + 1);
        prefs_editor.apply();
    }

    /**
     * Load JS file based on engine and inject it into WebView
     *
     * @param webView the destination WebView object
     * @param engine  used search engine
     */
    private void injectJS(WebView webView, int engine) {
        try {
            //select JS scheme based on engine
            String[] js = {"js/baidu.js", "js/bing.js"};

            //load JS file
            InputStream inputStream = getAssets().open(js[engine]);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);

            //inject the JS to webView
            webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load CSS file based on engine and inject it into WebView
     *
     * @param webView the destination WebView object
     * @param engine  used search engine
     */
    private void injectCSS(WebView webView, int engine) {
        try {
            //select CSS scheme based on engine
            String[] css = {"css/baidu.css", "css/bing.css"};

            //load CSS file
            InputStream inputStream = getAssets().open(css[engine]);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);

            //inject the CSS to webView
            webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Message");   //get the bundle message
        setKeyword(bundle.getString("keyword"));    //set the search key word
        setEngine(bundle.getInt("engine")); //set the search engine
        setPage(bundle.getInt("page")); //set the page of result to show

        //UI setting
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        BottomNavigationView navView = findViewById(R.id.nav_view); //find navigation on the bottom


        //TODO:not used

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_previous, R.id.navigation_home, R.id.navigation_next)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_result);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);


        WebView webView = (WebView) findViewById(R.id.webView);
        //allow JavaScript execution with H5 compatibility
        //configure web settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //Allow errors to enforce web pages
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        } else {
            webSettings.setMixedContentMode(WebSettings.LOAD_DEFAULT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //Allow JavaScript error
            try {
                Method method = Class.forName("android.webkit.WebView").
                        getMethod("setWebContentsDebuggingEnabled", Boolean.TYPE);
                if (method != null) {
                    method.setAccessible(true);
                    method.invoke(null, true);
                }
            } catch (Exception e) {
            }
        }
        webView.setWebViewClient(new WebViewClient() {
            //Allow WebView loading page
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(String.valueOf(request.getUrl()));
                return true;
            }

            //Fix certificate errors
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });


        /*
         * when first loading, it needs to get key word and search engine such parameters from HomeActivity
         * use initialized value here
         */
        setContent(webView, engine, keyword, page);


        //assign buttons of pre-page, next-page and home
        Button prev = (Button) findViewById(R.id.button_searchActivity_previousPage);
        Button home = (Button) findViewById(R.id.button_searchActivity_home);
        Button next = (Button) findViewById(R.id.button_searchActivity_nextPage);

        //set click event to pre-page, next-page and home buttons
        prev.setOnClickListener(new View.OnClickListener() {    //previous page
            @Override
            public void onClick(View v) {
                if (page == 0) return;
                setPage(page - 1);
                setContent(webView, engine, keyword, page);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {    //next page
            @Override
            public void onClick(View v) {
                setPage(page + 1);
                setContent(webView, engine, keyword, page);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {    //back to HomeActivity
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        //start searching action when key word input text has"search" operation
        EditText input = (EditText) findViewById(R.id.key_word_TextView);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //when the action key is "search" operation
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Editable editableinput = input.getText();    //get input key word
                    if (editableinput != null) {
                        keyword = editableinput.toString();

                        //start search the input key word
                        if (!keyword.equals("")) setContent(webView, engine, keyword, 1);
                    }
                    input.clearFocus(); //remove focus from input box
                    hideInput();    //hide the keyboard
                    return true;
                }
                return false;
            }
        });

        //start searching action when search button is clicked
        ImageButton search = (ImageButton) findViewById(R.id.search_imageButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editableinput = input.getText();    //get input key word
                if (editableinput != null) {
                    keyword = editableinput.toString();

                    //start search the input key word
                    if (!keyword.equals("")) setContent(webView, engine, keyword, 1);
                }
                input.clearFocus(); //remove focus from input box
                hideInput();    //hide soft keyboard
            }
        });

    }

    /**
     * Hide the soft keyboard
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}