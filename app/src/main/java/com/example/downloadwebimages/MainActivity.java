package com.example.downloadwebimages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    private EditText urlInput;
    private ProgressBar mProgressBar;
    private Button getSrcBtn;

    private String imageURLs;
    private String[] imageURLArray;

    private ArrayList<Bitmap> imageBitmaps = new ArrayList<Bitmap>();
    private ArrayList<File> destFiles = new ArrayList<File>();

    private ArrayList<String> destFileArray = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mWebView = findViewById(R.id.web_view);
        getSrcBtn = findViewById(R.id.getSrcBtn);
        gridView = findViewById(R.id.gridView);
        urlInput = findViewById(R.id.urlInput);


        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        getSrcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(MainActivity.this);
                externalUrl ="https://" + urlInput.getText().toString();
                System.out.println("External URL = " + externalUrl);
                if(Patterns.WEB_URL.matcher(externalUrl).matches()) {
                    Toast.makeText(MainActivity.this, "Beginning download...", Toast.LENGTH_LONG).show();
                    loadPage();
                }
                else {
                    Toast.makeText(MainActivity.this,"URL invalid",Toast.LENGTH_LONG).show();
                }


            }
        });





    }



    private void loadPage(){
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
        String[] tempStringArray = new String[20];
        int counter = 0;
        for (String url : imageURLArray) {
            if(counter > 19){
                break;
            }
            else if(url.contains(".jpg") || url.contains(".png"))
            {
                tempStringArray[counter] = url;
                counter++;
            }

        }
        imageURLArray = tempStringArray;
        System.out.println("After processing image URLs");
        for (String url : imageURLArray){
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

                // Delete existing images on SD card
                deleteExistingImgFiles();

                String destFilename;
                File destFile = null;
                File dir =getExternalFilesDir(Environment.DIRECTORY_PICTURES);

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            MyAdapter adapter = new MyAdapter(MainActivity.this);
                            if(gridView != null)
                            {
                                gridView.setAdapter(adapter);

                            }
                            }
                    });

            }

        }).start();

    }

    private void deleteExistingImgFiles(){
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

    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

}