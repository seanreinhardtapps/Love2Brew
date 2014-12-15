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
public class TabFrag2 extends Fragment {
    String[] data2;
//TODO Comments
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.act_tabbed_brewer2,container,false);

        TextView txtView1 = (TextView)rootView.findViewById(R.id.text2_1);
        TextView txtView2 = (TextView)rootView.findViewById(R.id.text2_2);

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
