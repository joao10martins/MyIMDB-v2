package com.example.myimdb;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private boolean isNowPlayingFragmentDisplayed = false;
    private boolean isHomeFragmentDisplayed = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (isNowPlayingFragmentDisplayed){
                        displayHome();
                        /*FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.popBackStack();
*/
                    }
                    displayHome();
                    return true;
                case R.id.navigation_nowplaying:
                    if (isHomeFragmentDisplayed){
                        displayNowPlaying();
                        /*FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.popBackStack();*/

                    }
                    displayNowPlaying();
                    return true;
                case R.id.navigation_search:
                    //mTextMessage.setText(R.string.title_search);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    // Display Now Playing Movies
    public void displayNowPlaying() {
        // Instantiate the fragment.
        NowPlayingFragment nowPlayingFragment = NowPlayingFragment.newInstance();
        // Get the FragmentManager and start a transaction.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Set transition animations
        fragmentTransaction.setCustomAnimations(R.anim.exit_to_left, R.anim.enter_from_left);

        // Add the NowPlayingFragment
        fragmentTransaction.replace(R.id.fragment_container,
                nowPlayingFragment);
        fragmentTransaction.addToBackStack(null).commit();

        // Set boolean flag to indicate fragment is open.
        isNowPlayingFragmentDisplayed = true;
    }


    // Display Home Screen
    public void displayHome() {
        // Instantiate the fragment.
        HomeFragment homeFragment = HomeFragment.newInstance();
        // Get the FragmentManager and start a transaction.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Set transition animations
        fragmentTransaction.setCustomAnimations(R.anim.exit_to_right, R.anim.enter_from_right);

        // Add the NowPlayingFragment
        fragmentTransaction.replace(R.id.fragment_container,
                homeFragment);
        fragmentTransaction.addToBackStack(null).commit();

        // Set boolean flag to indicate fragment is open.
        isHomeFragmentDisplayed = true;
    }

}
