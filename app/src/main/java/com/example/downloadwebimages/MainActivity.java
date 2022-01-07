package com.example.downloadwebimages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.StringReader;

public class MainActivity extends AppCompatActivity {
    //public static final String EXTERNAL_URL = "externalUrl";
    private String externalUrl;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private Button getSrcBtn;
    private Button printUrlBtn;
    private String imageURLs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        externalUrl = "https://stocksnap.io/";

        mWebView = findViewById(R.id.web_view);
        getSrcBtn = findViewById(R.id.getSrcBtn);
        printUrlBtn = findViewById(R.id.printImgUrlBtn);

        printUrlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printImageURLs();
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view,url);
                System.out.println("page finished loading");
                getSrcBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getImgURLs(mWebView);


                    }
                });
                getSrcBtn.setText("Get Image URLs");
                getSrcBtn.setBackgroundColor(Color.parseColor("#2a9d95"));


            }
        }

        );
        mWebView.loadUrl(externalUrl);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);




        mWebView.setWebChromeClient( new WebChromeClient() {
                                         @Override
                                         public void onProgressChanged( WebView webView, int newProgress) {
                 if(newProgress == 100) {
                     mProgressBar.setVisibility(View.GONE);
                 }
                 else {
                     mProgressBar.setVisibility(View.VISIBLE);
                     mProgressBar.setProgress(newProgress);
                 }
             }
         }


        );





    }

    public void getImgURLs(WebView webView) {

        String javascriptFn = "(function() {let imageNodes = document.querySelectorAll(\"img\"); " +
                              "let imageSrcs = new Array(); " +
                              "for (let i = 0; i < 20; i++) {imageSrcs.push(imageNodes[i].src);} " +
                              "return imageSrcs; })()";
        webView.evaluateJavascript(javascriptFn, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                System.out.println("Callback for getImgURLs");
                System.out.println(s);
                imageURLs = s;
            }
        });


    }

    public void printImageURLs() {
        System.out.println("Executing printImageURLs");
        System.out.println(imageURLs);
        try {
            System.out.println("String Length = " + imageURLs.length());
        }
        catch (Exception e) {
            System.out.println("Error. Probably not done loading");
        }
    }

}