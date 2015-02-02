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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

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
public class MainActivity extends ListActivity implements GetHttp.IGetHttpListener, SwitchBrewerListener{
    public static PendingIntent mCoffeeReceiverPendingIntent;
    public ArrayList<Brewer> hotBrewers = new ArrayList<Brewer>();
    public ArrayList<Brewer> coldBrewers = new ArrayList<Brewer>();
    public Context mContext;
    static AlarmManager mCoffeeAlarmManager;
    private SharedPreferences sharedPreferences;
    private String mPrefName = "BrewerData";
    private String sRESULTS = "stored_results";
    private BrewerViewAdapter mAdapter;
    public static Context context;
    SwitchBrewerListener sbListener;

    // Alarm Constants
    public static final long TWELVE_HOUR_ALARM_DELAY = 12* 60 * 60 * 1000;  // 12 Hr Alarm Const
    public static final long TWENTYFOUR_HOUR_ALARM_DELAY = 24* 60 * 60 * 1000;// 24 Hr Alarm Const
    public static final long FIVE_MIN_ALARM_DELAY = 5 * 60 * 1000;  // 5 Minute Alarm Const
    public static final long NINETY_SEC_ALARM_DELAY = 90 * 1000;  // 90 Second Alarm Const

    //Tag Constants for log calls
    public static final String MTAG = "Main Activity";
    public static final String ATAG = "Alarm Activity";

    //Hot and cold Brewer Control Tags
    public static final int HOT_BREWER_TAG = 101;
    public static final int COLD_BREWER_TAG = 102;

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
     * OpenBrewer()
     * Opens a new activity for the desired hot or cold brewer
     *
     * @param position - location in the Brewer ArrayList
     * @param tag - identifies hot or cold brewer
     */
    private void OpenBrewer(int position, int tag) {
        //Log.d(MTAG,"Trying to set an image");
        // Get brewer, Collect from hot or cold list based on selection
        //if position == 0, then it's the selection text, not a real brewer
        if (position != 0) {
            Brewer b;
            if (tag == HOT_BREWER_TAG)
                b = hotBrewers.get(position);
            else
                b = coldBrewers.get(position);

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
     *  -Checks for photos to download
     *
     * @param results - JSON Data String
     */
    @Override
    public void onGetHttpSuccess(String results) {
        Log.d(MTAG,results);
        AllUpdates(results);
        mAdapter.notifyDataSetChanged();
        DismissProgressFrag();
        ManagePhotoDownloads();

    }


    /**
     * AllUpdates()
     * De-serializes JSON Array into JSON objects
     * Then creates Brewer Objects for the JSON objects
     * Finally, calls the Spinner Loading Methods
     *
     * @param results - JSON Data String
     */
    private void AllUpdates(String results)
    {
        try {
            // de-serialization occurs in constructor - object is an array
            JSONArray jsonArray = new JSONArray(results);
            mAdapter.removeAllViews();
            hotBrewers.clear();
            coldBrewers.clear();
            for (int i = 0; i < jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Brewer brewer = new Brewer();

                brewer.setId(jsonObject.getInt("id"));
                brewer.setName(jsonObject.getString("name"));
                brewer.setTemp(jsonObject.getInt("temp"));
                brewer.setOverview(jsonObject.getString("overview"));
                brewer.setHowItWorks(jsonObject.getString("howItWorks"));
                brewer.setHistory(jsonObject.getString("history"));
                brewer.setSteps(jsonObject.getString("steps"));
                brewer.setRating(jsonObject.getInt("rating"));
                Log.d("MTAG", "Adding a new brewer "+brewer.getName());
                mAdapter.add(brewer);
                if (brewer.getTemp() == 1)
                    hotBrewers.add(brewer);
                else
                    coldBrewers.add(brewer);
            }
            // Add empty spinner items:
            Brewer empty = new Brewer();
            empty.setId(-1);
            empty.setName("--Select a Coffee Brewer--");
            hotBrewers.add(0,empty);
            coldBrewers.add(0,empty);

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
     * ManagePhotoDownloads()
     * Connects to the storage directory and reads the files loaded
     * Calls DownloadPicTask on all missing pics
     */
    private void ManagePhotoDownloads() {
        int i = 0;
        int b = hotBrewers.size()+coldBrewers.size()-2;
        String imagePath = "/Love2BrewData";
        File sdCard = Environment.getExternalStorageDirectory();
        File storageDir = new File(sdCard.getAbsolutePath()+imagePath);
        //Race Condition - ensure directory exists
        storageDir.mkdirs();
        File[] filelist = null;

        //get listing of all picture files
        if (storageDir.isDirectory() && storageDir.exists()) {
            filelist = storageDir.listFiles();
        }

        //Check if each file has been downloaded already
        if (filelist == null || filelist.length > 0) {
            for (int q = 0; q < b; q++) {

                //Log.d(MTAG,"HI "+filelist[i].getName());
                if (filelist[i].getName().equals("PNG_BREWER_" + q + ".png")) {
                    if (i != filelist.length)
                        i++;

                } else {
                    new DownloadPicTask().execute(q + 1);
                }
            }
        }
        //First time files are loaded
        else
            for (int h=0; h < b; h++)
                new DownloadPicTask().execute(h + 1);
    }


    /**
     * onListItemClick()
     * Prepares call to appropriate Coffee Brewer Activity
     * @param l - reference to listView
     * @param v - reference to View
     * @param position - position in ListView
     * @param id
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Brewer brewer = mAdapter.getItem(position);
        OpenBrewerWithObject(brewer);
    }


    /**
     * onCreateOptionsMenu()
     * Inflates Menu
     * @param menu
     * @return
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
     * @return
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


    /**
     * PopToast() Display quick messages to user
     * @param text - Message to be sent to Toast
     */
    public void PopToast(String text){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }


    /**
     * onYesClick() Callback method from AlarmDialogFrag to call PopToast
     * @param position - position in Brewer arraylist
     * @param tag - Hot or Cold Brewer
     */
    @Override
    public void onSwitch(int position,int tag) {
        OpenBrewer(position,tag);
    }


    /**
     * getHotBrewer() Callback to return ArrayList of Hot Coffee Brewers
     * @return ArrayList of Coffee Brewers
     */
    @Override
    public ArrayList<Brewer> getHotBrewer() {
        return hotBrewers;
    }


    /**
     * getColdBrewer() Callback to return ArrayList of Cold Coffee Brewers
     * @return ArrayList of Coffee Brewers
     */
    @Override
    public ArrayList<Brewer> getColdBrewer() {
        return coldBrewers;
    }

}// End - MainActivity Class


/**
 * DownloadPicTask Class
 * Class Hosts an Async Task used to download images from the web service
 */
class DownloadPicTask extends AsyncTask<Integer, Integer, Void> {


    /**
     * doInBackground()
     * Define filename
     * connect to pictures dictionary
     * open file stream and download bitmap
     * @param Pos - Position integer identifies which image # to download
     * @return - None
     */
    @Override
    protected Void doInBackground(Integer... Pos) {
        // Create an image file name
        int position = Pos[0];
        String imagePath = "/Love2BrewData";
        String imageFileName = "PNG_BREWER_" + position;
        String sourceFileName =
                "http://coffee.sreinhardt.com/Content/images/image"+position+".png";
        //Establish File Directory
        File sdCard = Environment.getExternalStorageDirectory();
        File storageDir = new File(sdCard.getAbsolutePath()+imagePath);
        storageDir.mkdirs();
        //Log.d(MainActivity.MTAG, "Directory: " + storageDir);
        //Create new file for image
        File file = new File(storageDir, imageFileName + ".png");

        //Declare bitmap and FileStream
        Bitmap bmp;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Begin download of image
        try {
            java.net.URL url = new java.net.URL(sourceFileName);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                //Log.d("AAA", "Good Connection");
                InputStream input = connection.getInputStream();
                bmp = BitmapFactory.decodeStream(input);
                //return myBitmap;
                if (bmp == null)
                    Log.d("AAA", "Null bitmap");
                else {
                    //Log.d("AAA", "Good bitmap");
                    bmp.compress(Bitmap.CompressFormat.PNG, 85, fos);
                    assert fos != null;
                    fos.flush();
                    fos.close();
                }
            }
            else
                Log.d("AAA", "Bad Connection");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}//end - DownloadPicTask Class


/**
 * DialogClickListener
 * Callback interface for DialogFragments
 */
interface SwitchBrewerListener {
    public void onSwitch(int position, int tag);
    public ArrayList<Brewer> getHotBrewer();
    public ArrayList<Brewer> getColdBrewer();
}