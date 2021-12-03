package org.happyhorse.naivesearch.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.happyhorse.naivesearch.R;

import java.lang.reflect.Method;

public class ResultActivity extends AppCompatActivity {

    private WebView webView;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        Bundle http_link_bundle = intent.getExtras();
        String url = http_link_bundle.getString("link");



        //webView = new WebView(this);
        webView = (WebView) findViewById(R.id.result_webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    view.loadUrl(url);
                    return false;
                }
                try {
                    webView.goBack(); // 页面尝试跳转到自家app时阻止并返回上一页
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }
}