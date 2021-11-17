//package org.happyhorse.naivesearch.ui;
//
//import android.content.Intent;
//import android.net.http.SslError;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.View;
//import android.webkit.SslErrorHandler;
//import android.webkit.WebResourceRequest;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.Button;
//import android.widget.EditText;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import org.happyhorse.naivesearch.R;
//
//import org.happyhorse.naivesearch.utils.SpyderUtil;
//
//import java.lang.reflect.Method;
//
//
//public class WebViewActivity extends AppCompatActivity {
//
//    private String keyword="hi";
//
//    private int engine=0;
//
//    private int page=1;
//
//    public void setKeyword(String keyword) {
//        this.keyword = keyword;
//    }
//
//    public void setEngine(int engine) {
//        this.engine = engine;
//    }
//
//    public void setPage(int page) {
//        this.page = page;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_web);
//        WebView webView=(WebView) findViewById(R.id.webView);
//        // 允许javascript执行与H5兼容
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setDomStorageEnabled(true);
//        //允许出错强制执行网页
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        } else {
//            webSettings.setMixedContentMode(WebSettings.LOAD_DEFAULT);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 允许javascript出错
//            try {
//                Method method = Class.forName("android.webkit.WebView").
//                        getMethod("setWebContentsDebuggingEnabled", Boolean.TYPE);
//                if (method != null) {
//                    method.setAccessible(true);
//                    method.invoke(null, true);
//                }
//            } catch (Exception e) {
//            }
//        }
//        webView.setWebViewClient(new WebViewClient(){
//            //允许webview加载页面
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                view.loadUrl(String.valueOf(request.getUrl()));
//                return true;
//            }
//            //解决证书错误
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                handler.proceed();
//            }
//        });
//        webView.loadUrl(SpyderUtil.urlMaker(engine,keyword,page));
//
//        //上一页和下一页以及主页按钮
//        Button prev=(Button) findViewById(R.id.prev);
//        Button next=(Button) findViewById(R.id.next);
//        Button home=(Button) findViewById(R.id.homebutton);
//        prev.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(page==0) return;
//                page=page-1;
//                webView.loadUrl(SpyderUtil.urlMaker(engine,keyword,page));
//            }
//        });
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                page=page+1;
//                webView.loadUrl(SpyderUtil.urlMaker(engine,keyword,page));
//            }
//        });
//        home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(WebViewActivity.this,HomeActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        //输入框内容获取以及搜索键
//        EditText input=(EditText) findViewById(R.id.input);
//        Button search=(Button) findViewById(R.id.search);
//        search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                keyword=input.getText().toString();
//                webView.loadUrl(SpyderUtil.urlMaker(engine,keyword,1));
//            }
//        });
//    }
//
//
//
//}
