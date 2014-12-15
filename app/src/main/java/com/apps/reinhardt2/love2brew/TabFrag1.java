package com.apps.reinhardt2.love2brew;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Sean on 11/8/2014.
 */
public class TabFrag1 extends Fragment {
    String[] data;
//TODO Comments
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //return inflater.inflate(R.layout.frag1,container,false);

        View rootView = inflater.inflate(R.layout.act_tabbed_brewer,container,false);

        ImageView imgView = (ImageView)rootView.findViewById(R.id.img1);
        TextView txtView1 = (TextView)rootView.findViewById(R.id.text1_1);
        TextView txtView2 = (TextView)rootView.findViewById(R.id.text1_2);

        if (getArguments().containsKey("Data"))
        {
            data = getArguments().getStringArray("Data");
        }

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) +"/"+ data[2] + ".png");
        Bitmap img = BitmapFactory.decodeFile(file.getAbsolutePath());
        imgView.setImageBitmap(img);
        Log.d("TAB1", "File:" + data[2]);
        Log.d("Tab1","Bit:"+file.getAbsolutePath());
        txtView1.setText(data[0]);
        txtView2.setText(data[1]);

        //TODO Load image

        return rootView;
    }
}
