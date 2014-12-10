package com.apps.reinhardt2.love2brew;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * Created by Sean on 12/7/2014.
 */
public class Brewer {


        private int id;
        private String name;
        private int temp;
        private String overview;
        private String howItWorks;
        private String history;
        private String steps;
        private byte[] imagePayload;
        private Bitmap decodedImage;

    public void setImagePayload(byte[] imagePayload) {
        this.imagePayload = imagePayload;
    }



    public Bitmap getDecodedImage() {
        return decodedImage;
    }

        public byte[] getImagePayload()
        {
            return imagePayload;
        }

        @Override
        public String toString() {
            return name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

    public void convertImage() {

            this.decodedImage = BitmapFactory.decodeByteArray(this.imagePayload, 0, this.imagePayload.length);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}


