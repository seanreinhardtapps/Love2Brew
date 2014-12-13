package com.apps.reinhardt2.love2brew;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*
    Main activity of Love2Brew App
    Implements GetHttp.IGetHttpListener to download JSON data of coffee brewers from web service
    Loads and displays spinners to select from hot or cold coffee brewers
    Selection of spinner opens another activity to display all information related to selection
    Menu bar contains button open dialog box for alarm
    Alarm dialog box allows user to register an alarm for a reminder

 */
public class MainActivity extends Activity implements GetHttp.IGetHttpListener{
    ImageView img;
    Spinner hotSpinner;
    Spinner coldSpinner;
    public List<Brewer> hotBrewers = new ArrayList<Brewer>();
    public List<Brewer> coldBrewers = new ArrayList<Brewer>();

    File photoFile;
    AlarmManager mCoffeeAlarmManager;
    private PendingIntent mCoffeeReceiverPendingIntent;
    private String mCurrentPhotoPath;

    // Alarm Constants
    private static final long TWELVE_HOUR_ALARM_DELAY = 12* 60 * 60 * 1000;  // 12 Hour Alarm Constant
    private static final long TWENTYFOUR_HOUR_ALARM_DELAY = 24* 60 * 60 * 1000;  // 24 Hour Alarm Constant
    private static final long FIVE_MIN_ALARM_DELAY = 5 * 60 * 1000;  // 5 Minute Alarm Constant
    private static final long NINETY_SEC_ALARM_DELAY = 90 * 1000;  // 90 Second Alarm Constant

    //Tag Constants for log calls
    public static final String MTAG = "Main Activity";
    public static final String TTAG = "Tabbed Activity";
    public static final String ATAG = "Alarm Activity";

    //Server Location
    public final String SERVER = "http://www.sanclementedev.org/Love2Brew/api/CoffeeBrewers/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Objects
        hotSpinner = (Spinner) findViewById(R.id.spnHot);
        coldSpinner = (Spinner) findViewById(R.id.spnCold);
        img = (ImageView)findViewById(R.id.img);

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

        // Perform network update, load
        checkUpdates();
    }

    private void OpenBrewer(int position) {

        Log.d(MTAG,"Trying to set an image");
        Intent intent = new Intent(this,TabbedBrewer.class);
        intent.putExtra("Brewer", position);
        startActivity(intent);
    }

    private void checkUpdates()
    {
        showProgressFrag("Checking For Updates...");
        GetHttp getHttp = new GetHttp();
        getHttp.setGetClientGetHttpListener(this);
        getHttp.setUrl(SERVER);
        getHttp.execute();
    }

    @Override
    public void onGetHttpSuccess(String results) {
        AllUpdates(results);
        DismissProgressFrag();
        ManagePhotoDownloads();
    }

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
                brewer.setImagePayload(jsonObject.getString("imagePayload").getBytes());
                brewer.convertImage();
                if (brewer.getTemp() == 1)
                    hotBrewers.add(brewer);
                else
                    coldBrewers.add(brewer);
            }
            LoadHotTempSpinner();
            LoadColdTempSpinner();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }




    private void ManagePhotoDownloads() {
        int i = 0;
        int b = hotBrewers.size()+coldBrewers.size();
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File[] filelist = null;
        //get listing of all picture files
        if (storageDir.isDirectory() && storageDir.exists()) {
            filelist = storageDir.listFiles();
        }

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

    private void LoadHotTempSpinner()
    {
        ArrayAdapter<Brewer> arrayAdapter = new ArrayAdapter<Brewer>(this,android.R.layout.simple_spinner_dropdown_item,hotBrewers);
        hotSpinner.setAdapter(arrayAdapter);
    }

    private void LoadColdTempSpinner()
    {

        ArrayAdapter<Brewer> arrayAdapter = new ArrayAdapter<Brewer>(this,android.R.layout.simple_spinner_dropdown_item,coldBrewers);
        coldSpinner.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.menu_top:
                Intent intent = new Intent(this,TabbedBrewer.class);

                // Launch the Activity using the intent
                startActivity(intent);
                break;
            case R.id.menu_alarm:
                // Set single alarm
                mCoffeeAlarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + FIVE_MIN_ALARM_DELAY,
                        mCoffeeReceiverPendingIntent);
                break;
            default:
                break;
        }
        return false;
    }

    private void showProgressFrag(String text)
    {
        DialogFragment dialogFragment = new ProcessFragActivity();
        Bundle bundle = new Bundle();
        bundle.putString("msg",text);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getFragmentManager(), "progressFrag");
    }

    private void DismissProgressFrag()
    {
        FragmentManager fragmentManager = getFragmentManager();
        DialogFragment dialogFragment = (DialogFragment)fragmentManager.findFragmentByTag("progressFrag");
        if (dialogFragment != null)
            dialogFragment.dismiss();
    }



}

class DownloadPicTask extends AsyncTask<Integer, Integer, Void> {


    @Override
    protected Void doInBackground(Integer... Pos) {
        // Create an image file name
        int position = Pos[0];
        String imageFileName = "/PNG_BREWER_" + position;
        String sourceFileName = "http://www.sanclementedev.org/Love2Brew/Content/images/image"+position+".png";
        Log.d("FILE", "Find Directory");
        Log.d("FILE", sourceFileName);
        //Find public pictures directory
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

    protected void onPostExecute() {
        // check this.exception
        // do something with the feed
        Log.d("AAA", "Finished?!");
    }
}