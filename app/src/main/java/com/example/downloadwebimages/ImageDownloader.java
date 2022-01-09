package com.example.downloadwebimages;

import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ImageDownloader {

    private WebView mWebView;
    private String externalUrl;

    protected void loadPage() {
        mWebView.setWebViewClient(new WebViewClient() {
                                      @Override
                                      public void onPageFinished(WebView view, String url) {
                                          super.onPageFinished(view,url);
                                          System.out.println("page finished loading");

                                          // Begin download process
                                          getImgURLs(mWebView);



                                      }
                                  }
        );
        System.out.println("External URL in loadPage() = " + externalUrl);
        mWebView.loadUrl(externalUrl);

    }

    public void getImgURLs(WebView webView) {
        String javascriptFn = "(function() {let imageNodes = document.querySelectorAll(\"img\"); " +
                "let imageSrcs = new Array(); " +
                "imageNodes.forEach(imageNode => imageSrcs.push(imageNode.src));" +
                "return imageSrcs; })()";
        webView.evaluateJavascript(javascriptFn, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                System.out.println("Callback for getImgURLs");
                externalUrl = null;
                imageURLs = s;
                printImageURLs();
                processImageUrlString();
                downloadImages();


            }
        });



    protected boolean downloadImage(String imgURL, File destFile) {
        try {
            URL url = new URL(imgURL);
            URLConnection conn = url.openConnection();

            InputStream in = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(destFile);

            byte[] buf = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }

            out.close();
            in.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
