package com.apps.reinhardt2.love2brew;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Sean on 11/8/2014.
 */
public class TabFrag3 extends Fragment {
    String[] data3;
//TODO Comments
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.act_tabbed_brewer3,container,false);

        TextView txtView3_1 = (TextView)rootView.findViewById(R.id.text3_2);

        if (getArguments().containsKey("Data"))
        {
            data3 = getArguments().getStringArray("Data");
            Log.d("TAB3", "Steps:" + data3[0]);
            txtView3_1.setText(data3[0]);
        }
        else
        {
            Log.d("NODATA", "Data is missing");
        }


        return rootView;
    }
}
