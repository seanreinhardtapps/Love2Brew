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

               /* try {
                    //createImageFile(position);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.d(MTAG, "Error in image file creation");
                }*/
                new DownloadPicTask().execute(position);

                //OpenBrewer(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //no action
            }
        });

        coldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OpenBrewer(position);
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

    private void OpenBrewer(int position) {

        Log.d(MTAG,"Trying to set an image");
        Intent intent = new Intent(this,TabbedBrewer.class);
        intent.putExtra("Brewer", position);
        startActivity(intent);
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


   // private File createImageFile(int position) throws IOException {


        /*FileOutputStream fos = new FileOutputStream(file);
        write your byteArray here
                fos.write(brewer.getImagePayload());
                fos.flush();
                fos.close();*/

        /*FileOutputStream fOut = new FileOutputStream(file);
        //Bitmap bmp = BitmapFactory.decodeByteArray(brewer.getImagePayload(), 0, brewer.getImagePayload().length);
        ByteArrayInputStream byt = new ByteArrayInputStream(brewer.getImagePayload());
        Bitmap bmp=BitmapFactory.decodeStream(byt);
        if (bmp == null)
            Log.d("AAA", "Null bitmap");
        else {
            bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();

        }
        */

       /* try {
            InputStream is = (InputStream) new URL("http://www.sanclementedev.org/Love2Brew/Content/images/image1.png").getContent();
            bmp = BitmapFactory.decodeStream(is);
            is.close();

        } catch (Exception e) {
            Log.d("AAA", "Did not connect");
            return null;
        }
        if (bmp == null)
            Log.d("AAA", "Null bitmap");
        else {
            Log.d("AAA", "Good bitmap");

            bmp.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
        }*/




        //return file;
    //}

}

class DownloadPicTask extends AsyncTask<Integer, Integer, Void> {


    @Override
    protected Void doInBackground(Integer... position) {
        // Create an image file name

        String imageFileName = "/PNG_BREWERf_" + position;
        Log.d("FILE", "Find Directory");

        //Find public pictures directory
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        Log.d(MainActivity.MTAG, "Directory: " + storageDir);

        //Call in case directory is missing - - not normally needed
        storageDir.mkdirs();

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + imageFileName + ".png");


       // Brewer brewer = MainActivity.hotBrewers.get(position);



        Bitmap bmp;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            java.net.URL url = new java.net.URL("http://www.sanclementedev.org/Love2Brew/Content/images/image1.png");
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