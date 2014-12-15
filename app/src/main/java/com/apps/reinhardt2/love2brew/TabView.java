package com.apps.reinhardt2.love2brew;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

/**
 * SEANREINHARDTAPPS
 * Created by Sean Reinhardt on 12/14/2014.
 * Java Android Application
 * This file is a module in the application:
 * Project host at https://www.github.com/SeanReinhardtApps/
 * <p/>
 * 2014
 */
public class TabView extends Activity {
    Brewer brewer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_brewer);
//TODO Comments
        Bundle bundle = getIntent().getExtras();

        Log.d("TABS","Name:"+bundle.getString("BName"));
        Log.d("TABS","Overview:"+bundle.getString("BOverview"));
        Log.d("TABS","File:"+ bundle.getString("BFile"));
        String[] t1 = {bundle.getString("BName"),bundle.getString("BOverview"),
                bundle.getString("BFile")};
        String[] t2 = {bundle.getString("BHistory"),bundle.getString("BHowWorks")};
        String[] t3 = {bundle.getString("BSteps")};

        Log.d("TABS","History:"+t2[0]);
        Log.d("TABS","HowItWorks:"+t2[1]);
        Log.d("TABS","Steps:"+t3[0]);

        ActionBar ab = getActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);



        ActionBar.Tab tab = ab.newTab()
                .setText(getString(R.string.title_section1))
                .setTabListener(new MyTabListener(this, TabFrag1.class.getName(),t1));
        ab.addTab(tab);

        tab = ab.newTab()
                .setText(getString(R.string.title_section2))
                .setTabListener(new MyTabListener(this, TabFrag2.class.getName(),t2));
        ab.addTab(tab);

        tab = ab.newTab()
                .setText(getString(R.string.title_section4))
                .setTabListener(new MyTabListener(this, TabFrag3.class.getName(),t3));
        ab.addTab(tab);


    }

    private class MyTabListener implements ActionBar.TabListener {

        private Fragment mFragment;
        private final Activity mActivity;
        private final String mFragName;
        private final String[] mData;


        public MyTabListener(Activity activity, String fragName, String[] data)
        {
            mActivity = activity;
            mFragName = fragName;
            mData = data;
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            if(mFragment==null)
            {
                Bundle bundle = new Bundle();
                bundle.putStringArray("Data",mData);
                mFragment=Fragment.instantiate(mActivity,mFragName,bundle);
                fragmentTransaction.add(android.R.id.content,mFragment);
            }
            else
            {
                fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out);
                fragmentTransaction.attach(mFragment);
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            if(mFragment != null)
            {
                fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out);
                fragmentTransaction.detach(mFragment);
            }
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            //nothing
        }
    }


}

