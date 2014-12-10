package com.apps.reinhardt2.love2brew;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements GetHttp.IGetHttpListener{
    ImageView img;
    Spinner hotSpinner;
    Spinner coldSpinner;
    public List<Brewer> hotBrewers = new ArrayList<Brewer>();
    public List<Brewer> coldBrewers = new ArrayList<Brewer>();

    File photoFile;
    AlarmManager mSelfieAlarmManager;
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

        hotSpinner = (Spinner) findViewById(R.id.spnHot);
        coldSpinner = (Spinner) findViewById(R.id.spnCold);
         img = (ImageView)findViewById(R.id.img);

        hotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SetAnImage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //no action
            }
        });

        coldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SetAnImage(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //no action
            }
        });


        // Get the AlarmManager Service
        mSelfieAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Create an Intent to broadcast to the SelfieAlarmReciever
        Intent mSelfieAlarmReceiverIntent = new Intent(this,
                AlarmNotificationReceiver.class);

        // Create an PendingIntent that holds the SelfieReceiverIntent
        mCoffeeReceiverPendingIntent = PendingIntent.getBroadcast(
                this, 0, mSelfieAlarmReceiverIntent, 0);

        // Set single alarm
        mSelfieAlarmManager.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + FIVE_MIN_ALARM_DELAY,
                mCoffeeReceiverPendingIntent);

        checkUpdates();
    }

    private void SetAnImage(int position) {
        Log.d(MTAG,"Trying to set an image");
        Brewer brewer = hotBrewers.get(position);

            Log.d(MTAG,"image"+ brewer.getImagePayload()[0] +" "+brewer.getImagePayload()[1]);
        img.setImageBitmap(brewer.getDecodedImage());
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

    private void checkUpdates()
    {
        showProgressFrag("Checking For Updates...");
        GetHttp getHttp = new GetHttp();
        getHttp.setGetClientGetHttpListener(this);
        getHttp.setUrl(SERVER);
        getHttp.execute();
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


    @Override
    public void onGetHttpSuccess(String results) {
        AllUpdates(results);
    }
}
