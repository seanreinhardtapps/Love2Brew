package com.apps.reinhardt2.love2brew;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * SEANREINHARDTAPPS
 * Created by Sean Reinhardt on 12/14/2014.
 * Java Android Application
 * This file is a module in the application:
 * Project host at https://www.github.com/SeanReinhardtApps/
 * <p/>
 * 2014
 */
public class TabFrag2 extends Fragment {
    String[] data2;
    /*****************************************************************************************
     onCreateView()
     -inflate view
     -Pull Data from bundle
     -Load text to textViews
     *****************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.act_tabbed_brewer2,container,false);
        TextView txtView1 = (TextView)rootView.findViewById(R.id.text2_2);
        TextView txtView2 = (TextView)rootView.findViewById(R.id.text2_4);

        if (getArguments().containsKey("Data"))
        {
            data2 = getArguments().getStringArray("Data");
            Log.d("TAB2", "History:" + data2[0]);
            txtView1.setText(data2[0]);
            txtView2.setText(data2[1]);
        }
        else
        {
            Log.d("NODATA","Data is missing");
        }


        return rootView;
    }
}
