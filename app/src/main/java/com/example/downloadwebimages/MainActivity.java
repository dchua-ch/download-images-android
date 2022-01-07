package com.example.downloadwebimages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.StringReader;

public class MainActivity extends AppCompatActivity {
    //public static final String EXTERNAL_URL = "externalUrl";
    private String externalUrl;
    private WebView mWebView;
    private Button getSrcBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        externalUrl = "https://stocksnap.io/";

        mWebView = findViewById(R.id.web_view);
        getSrcBtn = findViewById(R.id.getSrcBtn);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(externalUrl);

        getSrcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImgURLs(mWebView);

            }
        });



    }

    public void getImgURLs(WebView webView) {

        String javascriptFn = "(function() {let imageNodes = document.querySelectorAll(\"img\"); " +
                              "let imageSrcs = new Array(); " +
                              "for (let i = 0; i < 20; i++) {imageSrcs.push(imageNodes[i].src);} " +
                              "return imageSrcs; })()";
        webView.evaluateJavascript(javascriptFn, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                System.out.println(s);
            }
        });


    }

}