package com.example.android.hangupapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

/**
 * This class represents a custom BroadcastReceiver.  It detects the state of the phone app and sets values accordingly.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    public MyBroadcastReceiver() {
    }

    /**
     * This method is called whenever the app receives a PHONE_STATE action intent (as seen in the Manifest).
     * @param context the context of the broadcast receiver
     * @param intent the PHONE_STATE intent
     */
    public void onReceive(Context context, Intent intent) {

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        //set the calling value to true if the phone is off the hook
        if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
            SwitchFragment.calling = true;
        }
        else
            SwitchFragment.calling = false;
    }
}
