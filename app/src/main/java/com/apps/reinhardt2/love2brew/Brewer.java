package com.apps.reinhardt2.love2brew;


/**
 * SEANREINHARDTAPPS
 * Created by Sean Reinhardt on 10/19/2014.
 * Java Android Application
 * This file is a module in the application: Love2Brew
 * Project host at https://www.github.com/SeanReinhardtApps/Love2Brew
 *
 * Brewer Class
 * Model of Brewer - contains data and methods related to a coffee brewer
 *
 * 2014
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class Brewer {

        //Instance Variables
        private int     id;
        private String  name;
        private int     temp;
        private String  overview;
        private String  howItWorks;
        private String  history;
        private String  steps;
        private int     rating;
        private String  imageLocation;

    /**
     * Brewer Constructor
     * Only constructor allowed requires jsonObj as input
     * @param jsonObj - JSON Object downloaded from server
     */
    public Brewer(JSONObject jsonObj)
    {
        try
        {
            this.id = jsonObj.getInt("id");
            this.name = jsonObj.getString("name");
            this.temp = jsonObj.getInt("temp");
            this.overview = jsonObj.getString("overview");
            this.howItWorks = jsonObj.getString("howItWorks");
            this.history = jsonObj.getString("history");
            this.steps = jsonObj.getString("steps");
            this.rating = jsonObj.getInt("rating");
            this.imageLocation = "PNG_BREWER_" + id;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * downloadImgInBackground()
     * uses getImage method to check if img file exists
     * if image is null, creates a DownloadPicTask Async call to download image
     */
    public void downloadImgInBackground()
    {
        if (this.getImage() == null)
        {
            new DownloadPicTask().execute(this.getId());
            Log.d("Download","Download required");
        }
    }


    /**
     * getId
     * returns ID of brewer
     * @return int: Id of brewer in database
     */
    public int getId() {
        return id;
    }


    /**
     * getRating
     * returns rating of brewer - not an implemented feature
     * @return int: Rating
     */
    public int getRating() {
        return rating;
    }


    /**
     * getName
     * returns brewer name
     * @return String: Brewer name
     */
    public String getName() {
        return name;
    }

    /**
     * getTemp
     * returns integer flag for brewer temp 1 = Hot and 2 = Cold
     * @return int: 1 or 2
     */
    public int getTemp() {
        return temp;
    }


    /**
     * getOverview
     * returns overview text
     * @return String: Overview text
     */
    public String getOverview() {
        return overview;
    }


    /**
     * getHistory
     * returns history text
     * @return String: History text
     */
    public String getHistory() {
        return history;
    }


    /**
     * getHowItWorks
     * returns How It Works text
     * @return String: HowItWorks text
     */
    public String getHowItWorks() {
        return howItWorks;
    }


    /**
     * getSteps
     * returns Steps text
     * @return String: Steps text
     */
    public String getSteps() {
        return steps;
    }


    /**
     * getImageLocation
     * returns string representing file location of bitmap image on SD Card
     * @return String: Image Location on SD Card
     */
    public String getImageLocation() {
        return imageLocation;
    }


    /**
     * toString override
     * Returns name of brewer
     * @return - String: Name parameter
     */
    @Override
    public String toString() {
        return name;
    }


    /**
     * getImage()
     * finds Image on the SD Card and returns a Bitmap of it
     * @return - Bitmap object associated with brewer
     */
    public Bitmap getImage() {
        String imagePath = "/Love2BrewData";
        File sdCard = Environment.getExternalStorageDirectory();
        File storageDir = new File(sdCard.getAbsolutePath()+imagePath);
        File file = new File(storageDir +"/"+ getImageLocation() + ".png");
        //Load Bitmap to ImageView
        return BitmapFactory.decodeFile(file.getAbsolutePath());

    }
}// end - Brewer Class


