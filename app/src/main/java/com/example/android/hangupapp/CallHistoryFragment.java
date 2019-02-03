package com.example.android.hangupapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;



/**
 * A fragment that displays a RecyclerView to be populated with data from Database
 */
public class CallHistoryFragment extends Fragment {

    private CallAdapter callAdapter;

    public CallHistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_call_history, container, false);

        //put information from database into arrays for callAdapter
        String[] callTexts = new String[Database.getInstance().length()];
        for (int i=0;i<Database.getInstance().length();i++){
            callTexts[i]=Database.getInstance().getCall(i).getCallText();
        }

        callAdapter = new CallAdapter(callTexts);
        recyclerView.setAdapter(callAdapter);

        //set as a linear layout and return the recycler
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        return recyclerView;
    }

    /**
     * Method to update the RecyclerView
     * Takes data from Database and passes along to adapter, then notifies it
     */
    public void refreshData(){
        String[] callTexts = new String[Database.getInstance().length()];
        for (int i=0;i<Database.getInstance().length();i++){
            callTexts[i]=Database.getInstance().getCall(i).getCallText();
        }
        //https://stackoverflow.com/questions/31367599/how-to-update-recyclerview-adapter-data
        callAdapter.refreshData(callTexts);
        callAdapter.notifyDataSetChanged();
    }

}
