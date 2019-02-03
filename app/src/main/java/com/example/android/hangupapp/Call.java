package com.example.android.hangupapp;

/**
 * A class that stores information and is to be inserted and taken from Database
 */
public class Call {

    private String callText;         //the string containing information on the call

    Call(String callText){
        this.callText=callText;
    }

    /**
     * Method getter for callText
     * @return callText
     */
    public String getCallText(){
        return callText;
    }
}
