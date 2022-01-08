package com.example.downloadwebimages;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter {
    private final Context context;

    protected File dir;
    protected File[] existingFiles;
    protected ArrayList<Bitmap> imgBitmaps = new ArrayList<>();

    public MyAdapter(Context context) {
        super(context, R.layout.grid_element);
        this.context = context;

        dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        existingFiles = dir.listFiles();
        for (File imgFile : existingFiles) {
            Bitmap bitmap = null;

            try {
                if (imgFile.exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    System.out.println("Trying to decode imgFile: " + imgFile.getAbsolutePath());
                    bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if(bitmap != null){
                        System.out.println("Decoding successful");
                        imgBitmaps.add(bitmap);
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }




        }
        addAll(new Object[imgBitmaps.size()]);
    }

    public MyAdapter(Context context, ArrayList<Bitmap>  imgBitmaps) {
        super(context,R.layout.grid_element);
        this.context = context;
        this.imgBitmaps=imgBitmaps;

        addAll(new Object[20]);

    }

    @NonNull
    @Override
    public View getView(int pos, View view, @NonNull ViewGroup parent){

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_element,parent,false);
        }

        //set the image for ImageView
        ImageView imageView = view.findViewById(R.id.imageView);
        // seems to be tapping on array adapter?
        imageView.setImageBitmap(imgBitmaps.get(pos));



        return view;
    }
}
