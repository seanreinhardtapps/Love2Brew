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
 * SEANREINHARDTAPPS
 * Created by Sean Reinhardt on 12/5/2014.
 * Java Android Application
 * This file is a module in the application: Love2Brew
 * Project host at https://www.github.com/SeanReinhardtApps/Love2Brew
 *
 * GetHttp Async Class
 * Defines IGetHttpListener interface to download JSON data of coffee brewers from web service
 * Connects a HTTP Url connect to the web server and performs a GET
 * Uses a string buffer to receive JSON Array
 *
 * 2014
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

    /**
     * doInBackground()
     * connects to server
     * opens a connection and uses a buffered reader to collect the JSON string into a StringBuilder object
     * @param voids
     * @return - JSON Data String
     */
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
