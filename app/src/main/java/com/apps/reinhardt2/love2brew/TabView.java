package com.apps.reinhardt2.love2brew;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * SEANREINHARDTAPPS
 * Created by Sean Reinhardt on 12/14/2014.
 * Java Android Application
 * This file is a module in the application:
 * Project host at https://www.github.com/SeanReinhardtApps/
 *
 * 2014
 */
public class TabView extends Activity {
    Brewer brewer;
    /*****************************************************************************************
     onCreate()
     -inflate view
     -Register listeners for Hot and Cold Temp Spinners
     -Setup of Alarm Manager
     -Start Methods for JSON and Image Downloads
     *****************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_brewer);

        //Catch Bundle and extract data
        Bundle bundle = getIntent().getExtras();
        //Debug Logs for Bundle Data Receipt
        //Log.d("TABS", "Name:" + bundle.getString("BName"));
        //Log.d("TABS", "Overview:" + bundle.getString("BOverview"));
        //Log.d("TABS", "File:" + bundle.getString("BFile"));

        //Repackage Data into arrays to pass to appropriate tabs
        String[] t1 = {bundle.getString("BName"), bundle.getString("BOverview"),
                bundle.getString("BFile")};
        String[] t2 = {bundle.getString("BHistory"), bundle.getString("BHowWorks")};
        String[] t3 = {bundle.getString("BSteps")};

        //Log.d("TABS", "History:" + t2[0]);
        //Log.d("TABS", "HowItWorks:" + t2[1]);
        //Log.d("TABS", "Steps:" + t3[0]);

        //Activate tabs on action bar
        ActionBar ab = getActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //Instanciate the three tabs and provide data
        ActionBar.Tab tab = ab.newTab()
                .setText(getString(R.string.title_section1))
                .setTabListener(new MyTabListener(this, TabFrag1.class.getName(), t1));
        ab.addTab(tab);

        tab = ab.newTab()
                .setText(getString(R.string.title_section2))
                .setTabListener(new MyTabListener(this, TabFrag2.class.getName(), t2));
        ab.addTab(tab);

        tab = ab.newTab()
                .setText(getString(R.string.title_section4))
                .setTabListener(new MyTabListener(this, TabFrag3.class.getName(), t3));
        ab.addTab(tab);
    }
        /****************************************************************************************
         onCreateOptionsMenu()
         Inflates Menu
         ***************************************************************************************/
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_tabbed_brewer, menu);
            return true;
        }

        /****************************************************************************************
         onOptionsItemSelected()
         Alarm Button Opens dialog to set an alarm
         ***************************************************************************************/
        @Override
        public boolean onOptionsItemSelected(MenuItem item){
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            FragmentTransaction ft;
            switch (item.getItemId()) {
                case R.id.menu_mainmt:
                    finish();
                    break;
                case R.id.menu_aboutt:
                    ft = getFragmentManager().beginTransaction();
                    AboutFragActivity abtFragment = new AboutFragActivity();
                    abtFragment.show(ft,"Alarm");
                    break;
                case R.id.menu_alarmt:
                    ft = getFragmentManager().beginTransaction();
                    AlarmDialogFrag newFragment = new AlarmDialogFrag();
                    newFragment.show(ft, "Alarm");

                    break;
                default:
                    break;
            }
            return false;
        }

    /*****************************************************************************************
     myTabListener()
     Implements ActionBar.TabListener Interface
     -Receives Tab Data
     -Sets and loads all fragments from tab interface
     *****************************************************************************************/
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
                //Pass data array through bundle
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

