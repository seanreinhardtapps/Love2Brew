package com.apps.reinhardt2.love2brew;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
 *
 * 2014
 */

/**********************************************************************************************
    Main activity of Love2Brew App
    Implements GetHttp.IGetHttpListener to download JSON data of coffee brewers from web service
    Loads and displays spinners to select from hot or cold coffee brewers
    Selection of spinner opens another activity to display all information related to selection
    Menu bar contains button open dialog box for alarm
    Alarm dialog box allows user to register an alarm for a reminder

 ***********************************************************************************************/
public class MainActivity extends Activity implements GetHttp.IGetHttpListener, DialogClickListener{
    public static PendingIntent mCoffeeReceiverPendingIntent;
    Spinner hotSpinner;
    Spinner coldSpinner;
    public List<Brewer> hotBrewers = new ArrayList<Brewer>();
    public List<Brewer> coldBrewers = new ArrayList<Brewer>();

    static AlarmManager mCoffeeAlarmManager;
    //public PendingIntent mCoffeeReceiverPendingIntent;

    // Alarm Constants
    public static final long TWELVE_HOUR_ALARM_DELAY = 12* 60 * 60 * 1000;  // 12 Hr Alarm Const
    public static final long TWENTYFOUR_HOUR_ALARM_DELAY = 24* 60 * 60 * 1000;// 24 Hr Alarm Const
    public static final long FIVE_MIN_ALARM_DELAY = 5 * 60 * 1000;  // 5 Minute Alarm Const
    public static final long NINETY_SEC_ALARM_DELAY = 90 * 1000;  // 90 Second Alarm Const

    //Tag Constants for log calls
    public static final String MTAG = "Main Activity";
    public static final String ATAG = "Alarm Activity";

    //Server Location
    public final String SERVER = "http://www.sanclementedev.org/Love2Brew/api/CoffeeBrewers/";

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
        setContentView(R.layout.activity_main);

        // UI Objects
        hotSpinner = (Spinner) findViewById(R.id.spnHot);
        coldSpinner = (Spinner) findViewById(R.id.spnCold);

        // Register Event listeners for Hot Brewer Spinner
        hotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean init_hotSpinner = false;
            // Prevent selection upon loading with init_hotSpinner boolean
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!init_hotSpinner)
                init_hotSpinner = true;
            else
                OpenBrewer(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //no action
                //TODO Might fix problem with clicking first item in spinner here
            }
        });

        // Register Event listeners for Hot Brewer Spinner
        coldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean init_coldSpinner = false;
            // Prevent selection upon loading with init_hotSpinner boolean
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!init_coldSpinner)
                    init_coldSpinner = true;
                else
                    OpenBrewer(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //no action
            }
        });

        // Prepare the alarm service intents

        // Get the AlarmManager Service
        mCoffeeAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Create an Intent to broadcast to the AlarmReciever
        Intent mCoffeeAlarmReceiverIntent = new Intent(this,
                AlarmNotificationReceiver.class);

        // Create an PendingIntent that holds the ReceiverIntent
        mCoffeeReceiverPendingIntent = PendingIntent.getBroadcast(
                this, 0, mCoffeeAlarmReceiverIntent, 0);

        // Perform network update, download images required,
        // load spinners
        checkUpdates();
    }

    /*****************************************************************************************
     OpenBrewer()
     Opens a new activity for the desired hot or cold brewer
     *****************************************************************************************/
    private void OpenBrewer(int position) {

        Log.d(MTAG,"Trying to set an image");
        Intent intent = new Intent(this,TabView.class);
        // Bundle neeeded for extras
        Bundle bund = new Bundle();
        Brewer b = hotBrewers.get(position);
        bund.putString("BName",b.getName());
        bund.putString("BOverview",b.getOverview());
        bund.putString("BHistory",b.getHistory());
        bund.putString("BHowWorks",b.getHowItWorks());
        bund.putString("BSteps",b.getSteps());
        bund.putString("BFile",b.getImageLocation());
        // load extras to intent and start tabs
        intent.putExtras(bund);
        startActivity(intent);
    }

    /****************************************************************************************
     checkUpdates()
     Method handles calls related to downloads
     -Displays download dialog
     -Executes the GetHttp interface that downloads the JSON Data from web service
     ***************************************************************************************/
    private void checkUpdates()
    {
        showProgressFrag("Checking For Updates...");
        GetHttp getHttp = new GetHttp();
        getHttp.setGetClientGetHttpListener(this);
        getHttp.setUrl(SERVER);
        getHttp.execute();
    }

    /***************************************************************************************
     onGetHttpSuccess()
     Method called after Async Task for download is done
     -Processes JSON data
     -Dismisses Dialog
     -Checks for photos to download
     **************************************************************************************/
    @Override
    public void onGetHttpSuccess(String results) {
        AllUpdates(results);
        DismissProgressFrag();
        ManagePhotoDownloads();
    }

    /**************************************************************************************
     AllUpdates()
     De-serializes JSON Array into JSON objects
     Then creates Brewer Objects for the JSON objects
     Finally, calls the Spinner Loading Methods
     *************************************************************************************/
    private void AllUpdates(String results)
    {
        try {
            // de-serialization occurs in constructor - object is an array
            JSONArray jsonArray = new JSONArray(results);

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

                if (brewer.getTemp() == 1)
                    hotBrewers.add(brewer);
                else
                    coldBrewers.add(brewer);
            }
            //Load Spinners
            LoadHotTempSpinner();
            LoadColdTempSpinner();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /***********************************************************************************
     ManagePhotoDownloads()
     Connects to the storage directory and reads the files loaded
     Calls DownloadPicTask on all missing pics
     **********************************************************************************/
    private void ManagePhotoDownloads() {
        int i = 0;
        int b = hotBrewers.size()+coldBrewers.size();
        //Connect to the file directory
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File[] filelist = null;

        //get listing of all picture files
        if (storageDir.isDirectory() && storageDir.exists()) {
            filelist = storageDir.listFiles();
        }

        //Check if each file has been downloaded already
        for (int q = 0; q < b; q++)
        {
            Log.d(MTAG,"Q:"+q);
            Log.d(MTAG,"HI "+filelist[i].getName());
            if (filelist[i].getName().equals("PNG_BREWER_" + q + ".png"))
            {
                i++;
            }
            else
            {
                new DownloadPicTask().execute(q+1);
            }
        }

    }

    /****************************************************************************************
     LoadHotTempSpinner()
     Loads Hot temp Brewer List into the spinner
     ***************************************************************************************/
    private void LoadHotTempSpinner()
    {
        ArrayAdapter<Brewer> arrayAdapter = new ArrayAdapter<Brewer>
                (this,android.R.layout.simple_spinner_dropdown_item,hotBrewers);
        hotSpinner.setAdapter(arrayAdapter);
    }

    /****************************************************************************************
     LoadColdTempSpinner()
     Loads Cold temp Brewer List into the spinner
     ***************************************************************************************/
    private void LoadColdTempSpinner()
    {

        ArrayAdapter<Brewer> arrayAdapter = new ArrayAdapter<Brewer>
                (this,android.R.layout.simple_spinner_dropdown_item,coldBrewers);
        coldSpinner.setAdapter(arrayAdapter);
    }

    /****************************************************************************************
     onCreateOptionsMenu()
     Inflates Menu
     ***************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /****************************************************************************************
     onOptionsItemSelected()
     Alarm Button Opens dialog to set an alarm
     ***************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.menu_top:

                break;
            case R.id.menu_alarm:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                AlarmDialogFrag newFragment = new AlarmDialogFrag();
                newFragment.show(ft,"Alarm");

                break;
            default:
                break;
        }
        return false;
    }

    /****************************************************************************************
     showProgressFrag()
     Launches a dialog fragment
     ***************************************************************************************/
    private void showProgressFrag(String text)
    {
        DialogFragment dialogFragment = new ProcessFragActivity();
        Bundle bundle = new Bundle();
        bundle.putString("msg",text);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(), "progressFrag");
    }

    /****************************************************************************************
     DismissProgressFrag()
     Dismisses a dialog fragment
     ***************************************************************************************/
    private void DismissProgressFrag()
    {
        FragmentManager fragmentManager = getFragmentManager();
        DialogFragment dialogFragment =
                (DialogFragment)fragmentManager.findFragmentByTag("progressFrag");
        if (dialogFragment != null)
            dialogFragment.dismiss();
    }

    /****************************************************************************************
     PopToast() Display quick messages to user
     ***************************************************************************************/
    public void PopToast(String text){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onYesClick() {
        PopToast("HI");
    }
}// End - MainActivity Class


/****************************************************************************************
 DownloadPicTask Class
 Class Hosts an Async Task used to download images from the web service
 ***************************************************************************************/
class DownloadPicTask extends AsyncTask<Integer, Integer, Void> {

    /****************************************************************************************
     doInBackground()
     Define filename
     connect to pictures dictionary
     open file stream and download bitmap
     ***************************************************************************************/
    @Override
    protected Void doInBackground(Integer... Pos) {
        // Create an image file name
        int position = Pos[0];
        String imageFileName = "/PNG_BREWER_" + position;
        String sourceFileName =
                "http://www.sanclementedev.org/Love2Brew/Content/images/image"+position+".png";
        Log.d("FILE", "Find Directory");
        Log.d("FILE", sourceFileName);
        //Find public pictures directory
        //TODO Change Directory to private app directory
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        Log.d(MainActivity.MTAG, "Directory: " + storageDir);

        //Call in case directory is missing - - not normally needed
        storageDir.mkdirs();

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + imageFileName + ".png");

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
                Log.d("AAA", "Good Connection");
                InputStream input = connection.getInputStream();
                bmp = BitmapFactory.decodeStream(input);
                //return myBitmap;
                if (bmp == null)
                    Log.d("AAA", "Null bitmap");
                else {
                    Log.d("AAA", "Good bitmap");
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


/****************************************************************************************
 DialogClickListener
 Callback interface for DialogFragments
 ***************************************************************************************/
interface DialogClickListener {
    public void onYesClick();
}