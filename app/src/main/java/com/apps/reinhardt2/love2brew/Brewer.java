package com.apps.reinhardt2.love2brew;


/**
 * SEANREINHARDTAPPS
 * Created by Sean Reinhardt on 10/19/2014.
 * Java Android Application
 * This file is a module in the application: Love2Brew
 * Project host at https://www.github.com/SeanReinhardtApps/Love2Brew
 *
 * 2014
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;

/**********************************************************************************************
 Brewer Class
 Model of Brewer - contains data and methods related to a coffee brewer
 ***********************************************************************************************/
public class Brewer {

        //Instance Variables
        private int id;
        private String name;
        private int temp;
        private String overview;
        private String howItWorks;
        private String history;
        private String steps;
        private int rating;
        private String imageLocation;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    /******************************************************************************************
     Override of toString - Prints name of coffee brewer
     *****************************************************************************************/
    @Override
    public String toString() {
        return name;
    }

    /******************************************************************************************
     Public Getter Methods
     Public Setter Methods
     *****************************************************************************************/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.imageLocation = "PNG_BREWER_" + id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getHowItWorks() {
        return howItWorks;
    }

    public void setHowItWorks(String howItWorks) {
        this.howItWorks = howItWorks;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public Bitmap getImage() {
        String imagePath = "/Love2BrewData";
        File sdCard = Environment.getExternalStorageDirectory();
        File storageDir = new File(sdCard.getAbsolutePath()+imagePath);
        File file = new File(storageDir +"/"+ getImageLocation() + ".png");
        //Load Bitmap to ImageView
        Bitmap img = BitmapFactory.decodeFile(file.getAbsolutePath());
        return img;
    }
}// end - Brewer Class


