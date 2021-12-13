package org.happyhorse.naivesearch.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.happyhorse.naivesearch.R;

/**
 * A {@link AppCompatActivity} subclass.
 * This activity shows the website contents in a WebView when user click a searched out link in {@link SearchActivity}.
 * The web contents in this web view are out of application control, it means advertisements will not be affected by this application
 */
public class ResultActivity extends AppCompatActivity {

    private WebView webView;    //a web view used to show website
    private long exitTime = 0;  //TODO:not used

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //call super method
        setContentView(R.layout.activity_result);   //set the layout which will be shown
        //set default night mode to a night mode which uses always uses a dark mode
        //enabling night qualified resources regardless of the time.
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        //load the parameters
        Intent intent = getIntent();
        Bundle http_link_bundle = intent.getExtras();
        //get the link which user want to go with
        String url = http_link_bundle.getString("link");
        //webView = new WebView(this);

        //configure web view
        webView = (WebView) findViewById(R.id.result_webview);
        webView.getSettings().setJavaScriptEnabled(true);   //enable JS

        webView.loadUrl(url);   //load this url in webView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    view.loadUrl(url);
                    return false;
                }
                try {
                    //block the action that the page tries to jump to its own app
                    //and back to the previous page
                    webView.goBack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        //allow JavaScript execution with H5 compatibility
        //configure web settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        //enable search page zoom button
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }
}