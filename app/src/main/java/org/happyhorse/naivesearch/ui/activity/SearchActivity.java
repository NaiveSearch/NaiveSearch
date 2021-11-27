package org.happyhorse.naivesearch.ui.activity;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.happyhorse.naivesearch.R;
import org.happyhorse.naivesearch.config.Config;
import org.happyhorse.naivesearch.databinding.ActivitySearchBinding;
import org.happyhorse.naivesearch.utils.SpyderUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;

    private String keyword;

    private int engine;

    private int page;

    private String content;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setEngine(int engine) {
        this.engine = engine;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setContent(WebView webView, int engine, String keyword, int page) {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String unencodedHtml = bundle.getString("content");
                //String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(), Base64.NO_PADDING);
                //webView.loadData(encodedHtml, "text/html", "base64");
                webView.loadDataWithBaseURL(Config.SEARCH_ENGINE_URL[engine], unencodedHtml, "text/html", "UTF-8", null);

                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        injectCSS(webView, engine);
                        injectJS(webView, engine);
                        super.onPageFinished(view, url);
                    }

                    public boolean shouldOverrideUrlLoading(WebView view, String url)
                    {
                        Intent intent = new Intent(SearchActivity.this, ResultActivity.class);
                        Bundle http_link_bundle = new Bundle();
                        http_link_bundle.putString("link",url);
                        intent.putExtras(http_link_bundle);
                        startActivity(intent);
                        return true;
                    }

                });
            }
        };
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

    private void injectJS(WebView webView, int engine) {
        try {
            String[] js = {"js/baidu.js", "js/bing.js"};
            InputStream inputStream = getAssets().open(js[engine]);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
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

    private void injectCSS(WebView webView, int engine) {
        try {
            String[] css = {"css/baidu.css", "css/bing.css"};
            InputStream inputStream = getAssets().open(css[engine]);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
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
        //UI setting
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("Message");
        setKeyword(bundle.getString("keyword"));
        setEngine(bundle.getInt("engine"));
        setPage(bundle.getInt("page"));
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_previous, R.id.navigation_home, R.id.navigation_next)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_result);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);


        //Search function
        WebView webView = (WebView) findViewById(R.id.webView);
        // 允许javascript执行与H5兼容
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        //允许出错强制执行网页
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        } else {
            webSettings.setMixedContentMode(WebSettings.LOAD_DEFAULT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 允许javascript出错
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
            //允许webview加载页面
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(String.valueOf(request.getUrl()));
                return true;
            }

            //解决证书错误
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        /* 第一次加载 此处需要从HomeActivity获取keyword 和 Search Engine
         * 此处暂用初始化的值代替
         */
        setContent(webView, engine, keyword, page);


        //上一页和下一页以及主页按钮
        Button prev = (Button) findViewById(R.id.button_searchActivity_previousPage);
        Button home = (Button) findViewById(R.id.button_searchActivity_home);
        Button next = (Button) findViewById(R.id.button_searchActivity_nextPage);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page == 0) return;
                setPage(page - 1);
                setContent(webView, engine, keyword, page);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPage(page + 1);
                setContent(webView, engine, keyword, page);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        //输入框内容获取以及搜索键
        EditText input = (EditText) findViewById(R.id.key_word_TextView);
        ImageButton search = (ImageButton) findViewById(R.id.search_imageButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = input.getText().toString();
                setContent(webView, engine, keyword, 1);
                input.clearFocus();
                hideInput();
            }
        });


    }

    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}