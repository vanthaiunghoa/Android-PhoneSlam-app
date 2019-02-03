package com.example.android.hangupapp;


import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.provider.CallLog;
import android.telecom.TelecomManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.content.Context.SENSOR_SERVICE;

/**
 * This class represents the fragment that displays a switch and seek bar on the main activity.
 */
public class SwitchFragment extends Fragment implements SensorEventListener {

    public static boolean calling;    //tracking whether a call is being made (value is set in MyBroadcastReceiver)

    private MediaPlayer hangupNoise;    //stores a media file (mp3 in this app's case) to be played later

    private AudioManager audioManager;  //audioManager to set volume

    private int threshold=110;     //default threshold to end call

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_switch, container, false);
    }

    /**
     * Set up the functions of the fragment's views after it has been inflated.
     */
    @Override
    public void onStart() {
        super.onStart();
        setUpFragment();
    }

    /**
     * This method gets called to set up all components inside the fragment.
     */
    public void setUpFragment() {
        //the switch is set to toggle the broadcast receiver
        Switch sw = getActivity().findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //https://stackoverflow.com/questions/24266039/programmatically-enable-disable-broadcastreceiver
                    PackageManager pm  = getActivity().getPackageManager();
                    ComponentName componentName = new ComponentName(getActivity(), MyBroadcastReceiver.class);
                    pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                }
                else {
                    PackageManager pm  = getActivity().getPackageManager();
                    ComponentName componentName = new ComponentName(getActivity(), MyBroadcastReceiver.class);
                    pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                }
            }
        });

        //setting a listener to seekbar to change threshold to hang up
        SeekBar seekBar = getActivity().findViewById(R.id.thresholdSelector);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value=0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                threshold=20*value+50;//sets threshold from 50-250
            }
        });

        //constantly checking if the sensor may be activated (if the switch is toggled and the broadcast receiver is tracking a call)
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            int check = 0;

            @Override
            public void run() {
                if (calling && check == 0) {

                    //create the Sensor Manager
                    SensorManager sm = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);

                    //accelerometer sensor
                    Sensor mySensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                    //register Sensor listener
                    sm.registerListener(SwitchFragment.this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
                    check = 1;
                }
                else if (!calling)
                    check = 0;

                handler.postDelayed(this, 300);
            }
        });

        //audio file found on https://www.soundsnap.com from user Tom Hutchings,  Title:  Vintage 1976 Wheel Phone, Receiver Down, Hard, Slam
        hangupNoise = MediaPlayer.create(getActivity(), R.raw.landlinehangup);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);      //https://stackoverflow.com/questions/4178989/change-media-volume-in-android
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);             //middle parameter adjusts the volume
    }

    /**
     * This method is called whenever the accelerometer sensor detects movement of the phone.
     * @param event the object that holds information on the sensor's data
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //if x^2+y^2+z^2-9.81^2 > threshold, terminate call
        if ((Math.pow(event.values[0],2)+Math.pow(event.values[1],2)+Math.pow(event.values[2],2)-96.23161)>threshold) {

            //code to terminate a call found at https://stackoverflow.com/questions/18065144/end-call-in-android-programmatically/26162973
            TelecomManager tm = (TelecomManager) getActivity().getSystemService(Context.TELECOM_SERVICE);  //the TelecomManager is used to end the call

            if (tm != null) {

                tm.endCall();        //CALL ENDS HERE
                // success == true if call was terminated.

                hangupNoise.start();      //play the mp3 sound of a landline phone hanging up

                //pausing the execution for a second so that the cursor has time to update with the proper values for the most recent call
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                buildTextOutput();        //create the text containing information on the call
            }

            //create the Sensor Manager reference
            SensorManager sm = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
            sm.unregisterListener(this);       //need to unregister listener so that the onSensorChanged method doesn't get called after the phone call has ended
        }
    }

    /**
     * This method builds a String that is to be sent to a TextView (will later be in a Recycler View) that contains information on the call that just finished.
     */
    private void buildTextOutput() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        String callText = df.format(Calendar.getInstance().getTime());

        //getting the duration of the call from a cursor that queries the CallLog (still need to manually grant the permission from Settings in emulator)
        //https://stackoverflow.com/questions/16982894/how-to-get-the-outgoing-call-duration-in-real-time
        String[] strFields = {android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.DURATION};
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        try {
            Cursor mCallCursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    strFields, null, null, strOrder);
            mCallCursor.moveToFirst();

            //returns the phone number of the caller and format the number to (###) ###-####
            callText += "   " + mCallCursor.getString(0)
                    .replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");

            int totalSeconds = Integer.parseInt(mCallCursor.getString(1));
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;

            String callDuration = String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds);

            callText += "   " + callDuration;

            mCallCursor.close();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        Database.getInstance().addCall(new Call(callText));
        //for refreshing RecyclerView
        //bounces function call into MainActivity, then CallHistoryFragment, then CallAdapter and manually updates it
        //retakes data from Database to update it
        ((MainActivity)getActivity()).refreshData();
    }

    //implemented but not using
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
