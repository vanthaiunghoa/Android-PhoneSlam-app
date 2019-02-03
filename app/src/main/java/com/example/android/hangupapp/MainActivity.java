package com.example.android.hangupapp;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * This class represents the main activity of the application.
 */
public class MainActivity extends AppCompatActivity {

    private CallHistoryFragment callHistoryFragment;       //a reference to the fragment that holds the RecyclerView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //attaching PageAdapter to the ViewPager
        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager());
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(pageAdapter);

        //attaching the ViewPager to the TabLayout
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
    }
    /**
     * Method to update the RecyclerView
     * Is called in SwitchFragment and passes it along to the CallHistoryFragment
     */
    public void refreshData(){
        callHistoryFragment.refreshData();
    }

    private class PageAdapter extends FragmentPagerAdapter {

        public PageAdapter (FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;         //there will be two pages to swipe between
        }

        /**
         * This method places fragments into each tab.
         * @param position the position of the tab
         * @return the fragment contained within the tab
         */
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                     return new SwitchFragment();
                case 1:
                    callHistoryFragment = new CallHistoryFragment();
                    return callHistoryFragment;
            }
            return null;
        }

        /**
         * This method populates the tab view with titles.
         * @param position the tab position
         * @return the title displayed on the tab
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getText(R.string.home_tab);
                case 1:
                    return getResources().getText(R.string.log_tab);
            }
            return null;
        }
    }
}
