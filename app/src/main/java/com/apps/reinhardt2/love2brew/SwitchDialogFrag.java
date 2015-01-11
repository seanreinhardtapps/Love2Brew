package com.apps.reinhardt2.love2brew;


import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * SEANREINHARDTAPPS
 * Created by Sean Reinhardt on 1/11/2015.
 * Java Android Application
 * This file is a module in the application:
 * Project host at https://www.github.com/SeanReinhardtApps/
 * <p/>
 * 2015
 */
public class SwitchDialogFrag extends DialogFragment{

    SwitchBrewerListener switchBrewerListener; // local side of callback to open brewer
    Spinner hotSpinner;
    Spinner coldSpinner;

    /********************************************************************************************
     onCreateDialog()
     Dialog Builder is called to display Dialog Fragment
     *******************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.switch_dialog, container, false);

        getDialog().setTitle("Switch Brewers");

        // UI Objects
        hotSpinner = (Spinner) v.findViewById(R.id.spnHot);
        coldSpinner = (Spinner) v.findViewById(R.id.spnCold);

        // Register Event listeners for Hot Brewer Spinner
        hotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean init_hotSpinner = false;
            // Prevent selection upon loading with init_hotSpinner boolean
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!init_hotSpinner)
                    init_hotSpinner = true;
                else
                    switchBrewerListener.onSwitch(position, MainActivity.HOT_BREWER_TAG);
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
                    switchBrewerListener.onSwitch(position, MainActivity.COLD_BREWER_TAG);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //no action
            }
        });

        LoadColdTempSpinner();
        LoadHotTempSpinner();
        return v;
    }

    public void setGetClientGetHttpListener(SwitchBrewerListener clientswitchCallback) {
        this.switchBrewerListener = clientswitchCallback;
    }// client hook

    /****************************************************************************************
     LoadHotTempSpinner()
     Loads Hot temp Brewer List into the spinner
     ***************************************************************************************/
    private void LoadHotTempSpinner()
    {
        ArrayList hotBrewers = switchBrewerListener.getHotBrewer();
        ArrayAdapter<Brewer> arrayAdapter = new ArrayAdapter<Brewer>
                (this.getActivity(),android.R.layout.simple_spinner_dropdown_item,hotBrewers);
        hotSpinner.setAdapter(arrayAdapter);
    }

    /****************************************************************************************
     LoadColdTempSpinner()
     Loads Cold temp Brewer List into the spinner
     ***************************************************************************************/
    private void LoadColdTempSpinner()
    {
        ArrayList coldBrewers = switchBrewerListener.getColdBrewer();
        ArrayAdapter<Brewer> arrayAdapter = new ArrayAdapter<Brewer>
                (this.getActivity(),android.R.layout.simple_spinner_dropdown_item,coldBrewers);
        coldSpinner.setAdapter(arrayAdapter);
    }


}
