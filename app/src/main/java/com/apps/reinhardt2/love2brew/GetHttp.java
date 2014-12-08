package com.apps.reinhardt2.love2brew;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Sean on 11/24/2014.
 */
public class GetHttp extends AsyncTask<Void, Void, String> {
    public interface IGetHttpListener {
        public void onGetHttpSuccess(String results);
    }//end of interface

    IGetHttpListener iGetHttpListener; // local side of listener

    public void setGetClientGetHttpListener(IGetHttpListener clientGetHttpListener) {
        this.iGetHttpListener = clientGetHttpListener;
    }// client hook

    private String mUrl = "";

    public void setUrl (String ConnectTo) {
        mUrl = ConnectTo;
    }// url where file is download FROM

    @Override
    protected String doInBackground(Void... voids) {
        String results = "";
        URL url = null;

        try {
            url = new URL(mUrl);
            HttpURLConnection c = (HttpURLConnection)url.openConnection();
            c.setRequestMethod("GET");
            c.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String thisLine = null;
            while ((thisLine=bufferedReader.readLine())!= null)
            {
                stringBuilder.append(thisLine + "\n");
            }
            bufferedReader.close();
            results=stringBuilder.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    } // end of doInBackground

    @Override
    protected void onPostExecute(String results) {
        iGetHttpListener.onGetHttpSuccess(results);
    }//trigger local hook
}//GetHttp
