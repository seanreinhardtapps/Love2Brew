package com.apps.reinhardt2.love2brew;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * DownloadPicTask Class
 * Class Hosts an Async Task used to download images from the web service
 */
class DownloadPicTask extends AsyncTask<Integer, Integer, Void> {
    public interface PicDownloadListener {
        public void onPicDownloadSuccess();
    }//end of interface

    PicDownloadListener iPicDownloadListener; // local side of listener

    //public void setGetClientGetHttpListener(PicDownloadListener clientListener) {
    //    this.iPicDownloadListener = clientListener;
    //}// client hook

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

    @Override
    protected void onPostExecute(Void aVoid) {
        iPicDownloadListener.onPicDownloadSuccess();
    } // trigger response in Main Activity

}//end - DownloadPicTask Class