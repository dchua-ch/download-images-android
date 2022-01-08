package com.example.downloadwebimages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.GridView;
import android.widget.ImageView;
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
    private GridView gridView;
    private ProgressBar mProgressBar;
    private Button getSrcBtn;

    private String imageURLs;
    private String[] imageURLArray;
    private ArrayList<Bitmap> imageBitmaps;
    private ArrayList<File> destFiles;

    private ArrayList<String> destFileArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        externalUrl = "https://stocksnap.io/";

        mWebView = findViewById(R.id.web_view);
        getSrcBtn = findViewById(R.id.getSrcBtn);
        gridView = findViewById(R.id.gridView);


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
                imageURLs = s;
                printImageURLs();
                processImageUrlString();
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
                File destFile = null;
                File dir =getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
                    destFilename =  "image_" + df.format(counter);
                    counter++;
                    System.out.println("Downloading image from: " + imgURL);

                    destFile = new File(dir,destFilename);


                    if(downloader.downloadImage(imgURL,destFile))
                    {
                        System.out.println("Downloaded file: " + destFilename);


                    }

                }
                boolean finishedDownloading = true;

                if(finishedDownloading) {
                    System.out.println("Starting runOnUiThread");
                    File finalDestFile = destFile;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                            File[] existingFiles = dir.listFiles();
                            // note that with each reload, stocksnap images may change o.o
                            System.out.println("Trying to decode file: " + existingFiles[18].getAbsolutePath());
                            Bitmap bitmap = BitmapFactory.decodeFile(existingFiles[18].getAbsolutePath());

                            ImageView imageView = findViewById(R.id.imageViewTest);
                            imageView.setImageBitmap(bitmap);
                            //imageBitmaps.add(bitmap);

                            /*File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                            File[] existingFiles;
                            existingFiles = dir.listFiles();
                            System.out.println("Existing files length = " + existingFiles.length);
                            for (File imgFile : existingFiles) {

                                try{
                                    if(imgFile.exists()){
                                        System.out.println("Trying to decode imgFile: " + imgFile.getAbsolutePath());
                                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                        //imageBitmaps.add(bitmap);

                                    }
                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                }


                                //imageBitmaps.add(bitmap);
                            }
                            MyAdapter adapter = new MyAdapter(MainActivity.this,imageBitmaps);
                            if(gridView != null)
                            {
                                gridView.setAdapter(adapter);

                            }*/
                            MyAdapter adapter = new MyAdapter(MainActivity.this);
                            if(gridView != null)
                            {
                                gridView.setAdapter(adapter);

                            }
                            }
                    });
                }
            }

        }).start();

    }

}