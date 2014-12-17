package com.apps.reinhardt2.love2brew;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * SEANREINHARDTAPPS
 * Created by Sean Reinhardt on 12/14/2014.
 * Java Android Application
 * This file is a module in the application:
 * Project host at https://www.github.com/SeanReinhardtApps/
 * <p/>
 * 2014
 */
public class TabFrag1 extends Fragment {
    String[] data;
    /*****************************************************************************************
     onCreateView()
     -inflate view
     -Pull Data from bundle
     -Load text to textViews
     -Locate image file from storage location
     -Load image to imageView
     *****************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.act_tabbed_brewer,container,false);
        ImageView imgView = (ImageView)rootView.findViewById(R.id.img1);
        TextView txtView1 = (TextView)rootView.findViewById(R.id.text1_1);
        TextView txtView2 = (TextView)rootView.findViewById(R.id.text1_2);

        if (getArguments().containsKey("Data"))
        {
            data = getArguments().getStringArray("Data");
        }
        //Locate Image file from storage location
        String imagePath = "/Love2BrewData";
        File sdCard = Environment.getExternalStorageDirectory();
        File storageDir = new File(sdCard.getAbsolutePath()+imagePath);
        File file = new File(storageDir +"/"+ data[2] + ".png");
        //Load Bitmap to ImageView
        Bitmap img = BitmapFactory.decodeFile(file.getAbsolutePath());
        imgView.setImageBitmap(img);
        Log.d("TAB1", "File:" + data[2]);
        Log.d("Tab1","Bit:"+file.getAbsolutePath());
        //Load TextViews
        txtView1.setText(data[0]);
        txtView2.setText(data[1]);

        return rootView;
    }
}
