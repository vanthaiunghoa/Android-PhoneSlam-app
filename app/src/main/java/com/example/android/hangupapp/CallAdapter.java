package com.example.android.hangupapp;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * An adapter for the RecyclerView in CallHistoryFragment
 */
public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ViewHolder>
{
    private String[] callTexts;         //array holding all the strings of call information

    /**
     * Class that is used to hold CardViews
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;

        public ViewHolder(CardView v){
            super(v);
            cardView=v;
        }
    }

    /**
     * Method to update the callTexts inside this adapter
     * @param callTexts which holds updated data from Database
     */
    public void refreshData(String[] callTexts){
        this.callTexts=callTexts;
    }


    public CallAdapter(String[] callTexts){
        this.callTexts=callTexts;
    }

    /**
     * Method that is called to get the number of items in the RecyclerView
     * @return callTexts.length
     */
    @Override
    public int getItemCount(){
        return callTexts.length;
    }

    /**
     * Method that is called to create a ViewHolder for a CardView
     * @param parent
     * @param viewType
     * @return ViewHolder(cardView)
     */
    @Override
    public CallAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_call, parent, false);
        return new ViewHolder(cardView);
    }

    /**
     * Method to set the values of elements on the CardView
     * @param holder the given holder of the CardView
     * @param position the position in the RecyclerView of the given holder
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        CardView cardView = holder.cardView;
        TextView textView = (TextView) cardView.findViewById(R.id.text1);
        textView.setText(callTexts[position]);
    }
}
