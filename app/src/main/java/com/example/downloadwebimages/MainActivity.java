package com.example.downloadwebimages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.File;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //public static final String EXTERNAL_URL = "externalUrl";
    private String externalUrl;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private Button getSrcBtn;

    private String imageURLs;
    private String[] imageURLArray;

    private ArrayList<String> destFileArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        externalUrl = "https://stocksnap.io/";

        mWebView = findViewById(R.id.web_view);
        getSrcBtn = findViewById(R.id.getSrcBtn);


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
                //System.out.println(s);
                imageURLs = s;
                printImageURLs();
                processImageUrlString();
                //printImageURLs();
                downloadImages();

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
    public void processImageUrlString() {


        imageURLArray = imageURLs.replace("\"","")
                                 .replace("[","")
                                 .replace("]","")
                                 .split(",");
        System.out.println("Length of imageURLArray = " + imageURLArray.length);
        for (String url : imageURLArray) {
            System.out.println(url);
        }

    }
    public void downloadImages() {
        System.out.println("Executing downloadImages()...");
        ImageDownloader downloader = new ImageDownloader();


        new Thread (new Runnable() {
            @Override
            public void run() {
                System.out.println("entering new thread");
                String destFilename;
                File destFile;
                File dir =getExternalFilesDir(Environment.DIRECTORY_PICTURES);;
                File[] existingFiles = dir.listFiles();
                // Delete existing images from directory
                for(File file : existingFiles) {
                    try{
                        if(file.exists()) {
                            System.out.print("Deleting file : " + file.getName());
                            boolean result = file.delete();
                            if(result) {
                                System.out.println(", successful");
                            }
                        }
                    }
                    catch (Exception e) {
                        System.out.println("Error while deleting file");
                    }
                }
                int counter = 0;
                DecimalFormat df = new DecimalFormat("00");
                for (String imgURL : imageURLArray) {
                    //destFilename =  "image_" + df.format(counter) + "_" + UUID.randomUUID().toString() + imgURL.lastIndexOf(".") + 1;
                    destFilename =  "image_" + df.format(counter);
                    counter++;
                    System.out.println("Downloading image from: " + imgURL);

                    destFile = new File(dir,destFilename);

                    if(downloader.downloadImage(imgURL,destFile))
                    {
                        System.out.println("Downloaded file: " + destFilename);
                    }

                }
            }

        }).start();

    }

}