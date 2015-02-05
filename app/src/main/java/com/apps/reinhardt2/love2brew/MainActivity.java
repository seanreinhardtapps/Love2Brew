package com.apps.reinhardt2.love2brew;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * SEANREINHARDTAPPS
 * Created by Sean Reinhardt on 10/19/2014.
 * Java Android Application
 * This file is a module in the application: Love2Brew
 * Project host at https://www.github.com/SeanReinhardtApps/Love2Brew
 * Main activity of Love2Brew App
 * Implements GetHttp.IGetHttpListener to download JSON data of coffee brewers from web service
 * Extends ListActivity
 * Row selection opens another activity to display all information related to selection
 * Menu bar contains button open dialog box for alarm
 * Alarm dialog box allows user to register an alarm for a reminder
 *
 * 2014
 */
public class MainActivity extends ListActivity implements GetHttp.IGetHttpListener, DownloadPicTask.PicDownloadListener {
    public static PendingIntent mCoffeeReceiverPendingIntent;
    public Context mContext;
    static AlarmManager mCoffeeAlarmManager;
    private SharedPreferences sharedPreferences;
    private String mPrefName = "BrewerData";
    private String sRESULTS = "stored_results";
    private BrewerViewAdapter mAdapter;
    public static Context context;

    // Alarm Constants
    public static final long TWELVE_HOUR_ALARM_DELAY = 12* 60 * 60 * 1000;  // 12 Hr Alarm Const
    public static final long TWENTYFOUR_HOUR_ALARM_DELAY = 24* 60 * 60 * 1000;// 24 Hr Alarm Const
    public static final long FIVE_MIN_ALARM_DELAY = 5 * 60 * 1000;  // 5 Minute Alarm Const
    public static final long NINETY_SEC_ALARM_DELAY = 90 * 1000;  // 90 Second Alarm Const

    //Tag Constants for log calls
    public static final String MTAG = "Main Activity";


    //Server Location
    public final String SERVER = "http://coffee.sreinhardt.com/api/CoffeeBrewers/";


   /**
    * onCreate()
    * -inflate view
    * -instanciates listView and footer view
    * -loads shared preferences data
    * -Setup of Alarm Manager
    * -Start Methods for JSON and Image Downloads
    *
    * @param savedInstanceState - bundle with instance data from config changes
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);
        mContext = getBaseContext();
        context = this;
        mAdapter = new BrewerViewAdapter(getApplicationContext());


        //Attach footer view to download updates
        View footerView =  ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);
        ListView list = (ListView)findViewById(android.R.id.list);
        list.addFooterView(footerView);

        //set the list adapter
        setListAdapter(mAdapter);

        // Prepare the alarm service intents
        // Get the AlarmManager Service
        mCoffeeAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Create an Intent to broadcast to the AlarmReceiver
        Intent mCoffeeAlarmReceiverIntent = new Intent(this,
                AlarmNotificationReceiver.class);

        // Create an PendingIntent that holds the ReceiverIntent
        mCoffeeReceiverPendingIntent = PendingIntent.getBroadcast(
                this, 0, mCoffeeAlarmReceiverIntent, 0);



        //Load Shared Preferences
        //Get JSON String from storage, if exists launch AllUpdates Method to load brewers
        sharedPreferences = getSharedPreferences(mPrefName,MODE_PRIVATE);
        String temp_results = sharedPreferences.getString(sRESULTS,"");
        if (!"".equals(temp_results))
        {
            AllUpdates(temp_results);
        }
        else {
            // Perform network update, download images required,
            checkUpdates();
        }
        mAdapter.notifyDataSetChanged();

    }


   /**
    * footerClick()
    * Event handler for list's footer click, calls checkUpdates method
    *
    * @param v - View which contains footer
    */
    public void footerClick(View v)
    {
        checkUpdates();
    }


    /**
     * onResume()
     * Called when app reloads to view
     * -Check for downloaded data
     * -call AllUpdates method to load data
     */
    @Override
    protected void onResume() {
        super.onResume();
        //Load Shared Preferences
        //Get JSON String from storage, if exists launch AllUpdates Method to load brewers
        sharedPreferences = getSharedPreferences(mPrefName,MODE_PRIVATE);
        String temp_results = sharedPreferences.getString(sRESULTS,"");
        if (!"".equals(temp_results))
        {
            AllUpdates(temp_results);
        }
        mAdapter.notifyDataSetChanged();
    }


    /**
     * OpenBrewerWithObject()
     * Opens a new activity for the desired hot or cold brewer by receiving Brewer object
     *
     * @param b - Coffee Brewer object to send to Activity
     */
    private void OpenBrewerWithObject(Brewer b){
        //Prepare intent and load with data
        Intent intent = new Intent(this, TabView.class);
        // Bundle needed for extras
        Bundle bund = new Bundle();
        bund.putString("BName", b.getName());
        bund.putString("BOverview", b.getOverview());
        bund.putString("BHistory", b.getHistory());
        bund.putString("BHowWorks", b.getHowItWorks());
        bund.putString("BSteps", b.getSteps());
        bund.putString("BFile", b.getImageLocation());

        // load extras to intent and start tabs
        intent.putExtras(bund);
        startActivity(intent);
    }


    /**
     * checkUpdates()
     * Method handles calls related to downloads
     * -Displays download dialog
     * -Executes the GetHttp interface that downloads the JSON Data from web service
     */
    private void checkUpdates()
    {
        showProgressFrag("Checking For Updates...");
        GetHttp getHttp = new GetHttp();
        getHttp.setGetClientGetHttpListener(this);
        getHttp.setUrl(SERVER);
        getHttp.execute();
    }


    /**
     * onGetHttpSuccess()
     * Method called after Async Task for download is done
     * -Processes JSON data
     *  -Dismisses Dialog
     *
     * @param results - JSON Data String
     */
    @Override
    public void onGetHttpSuccess(String results) {
        Log.d(MTAG,results);
        AllUpdates(results);
        mAdapter.notifyDataSetChanged();
        DismissProgressFrag();
    }


    /**
     * AllUpdates()
     * De-serializes JSON Array into JSON objects
     * Then creates Brewer Objects for the JSON objects
     * Adds objects to ListViews internal list
     * Then adds JSON String to shared preferences
     *
     * @param results - JSON Data String
     */
    private void AllUpdates(String results)
    {
        try
        {
            // de-serialization occurs in constructor - object is an array
            JSONArray jsonArray = new JSONArray(results);
            mAdapter.removeAllViews();

            for (int i = 0; i < jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Brewer brewer = new Brewer(jsonObject);
                Log.d("MTAG", "Adding a new brewer "+brewer.getName());
                brewer.downloadImgInBackground();
                mAdapter.add(brewer);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        //Store download in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(sRESULTS,results);
        editor.apply();
    }


    /**
     * onListItemClick()
     * Prepares call to appropriate Coffee Brewer Activity
     * @param l - reference to listView
     * @param v - reference to View
     * @param position - position in ListView
     * @param id -
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Brewer brewer = mAdapter.getItem(position);
        OpenBrewerWithObject(brewer);
    }


    /**
     * onCreateOptionsMenu()
     * Inflates Menu
     * @param menu reference to menu object
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * onOptionsItemSelected()
     * Alarm Button Opens dialog to set an alarm
     * @param item - reference to menu item selected
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        FragmentTransaction ft;
        switch (item.getItemId())
        {
            case R.id.menu_top:
                //about dialog
                ft = getFragmentManager().beginTransaction();
                AboutFragActivity abtFragment = new AboutFragActivity();
                abtFragment.show(ft,"Alarm");
                break;
            case R.id.menu_alarm:
                //Launch Alarm Dialog Fragment
                ft = getFragmentManager().beginTransaction();
                AlarmDialogFrag newFragment = new AlarmDialogFrag();
                newFragment.show(ft,"Alarm");

                break;
            default:
                break;
        }
        return false;
    }


    /**
     * showProgressFrag()
     * Launches a dialog fragment
     * @param text - Message to be passed to dialog fragment
     */
    private void showProgressFrag(String text)
    {
        DialogFragment dialogFragment = new ProcessFragActivity();
        Bundle bundle = new Bundle();
        bundle.putString("msg",text);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(), "progressFrag");
    }


    /**
     * DismissProgressFrag()
     * Dismisses a dialog fragment
     */
    private void DismissProgressFrag()
    {
        FragmentManager fragmentManager = getFragmentManager();
        DialogFragment dialogFragment =
                (DialogFragment)fragmentManager.findFragmentByTag("progressFrag");
        if (dialogFragment != null)
            dialogFragment.dismiss();
    }


    @Override
    public void onPicDownloadSuccess() {
        Log.d("Download", "Listener Alerted");
        mAdapter.notifyDataSetChanged();
    }
}// End - MainActivity Class


