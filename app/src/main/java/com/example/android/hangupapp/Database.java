package com.example.android.hangupapp;

import java.util.ArrayList;

/**
 * A class that stores Calls as a singleton wrapper for an ArrayList
 */
class Database {
    private static final Database ourInstance = new Database();

    private ArrayList<Call> arrayList = new ArrayList<>();
    static Database getInstance() {
        return ourInstance;
    }

    private Database() {
    }

    /**
     * Method that adds a call to the ArrayList
     * @param call a new call to be added
     */
    public void addCall(Call call){
        arrayList.add(call);
    }

    /**
     * Method to get an individual call from the ArrayList
     * @return arrayList.get(i)
     */
    public Call getCall(int i){
        return arrayList.get(i);
    }

    /**
     * Method that returns the number of elements in the ArrayList
     * @return arrayList.size
     */
    public int length(){
        arrayList.trimToSize();
        return arrayList.size();
    }
}
