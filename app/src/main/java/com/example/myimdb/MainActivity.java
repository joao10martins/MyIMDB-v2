package com.example.myimdb;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SearchFragment.CheckKeyboardFocus {

    private EditText mSearch;
    private boolean isNowPlayingFragmentDisplayed = false;
    private boolean isHomeFragmentDisplayed = false;
    private boolean isSearchFocused = false;
    private View mView;

    private BottomNavigationView mBottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (isHomeFragmentDisplayed){
                        return true;
                    }
                    if (isNowPlayingFragmentDisplayed){
                        displayHome();
                    }
                    return true;
                case R.id.navigation_nowplaying:
                    if (isNowPlayingFragmentDisplayed){
                        return true;
                    }
                    if (isHomeFragmentDisplayed){
                        displayNowPlaying();

                    }
                    //displayNowPlaying();
                    return true;
                case R.id.navigation_search:
                    displaySearch();
                    //checkKeyboardFocus(mSearch);
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



        mBottomNavigationView = findViewById(R.id.navigation);


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        // Get the FragmentManager and start a transaction.
        HomeFragment homeFragment = HomeFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the fragment
        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
        fragmentTransaction.commit();
        isHomeFragmentDisplayed = true;
    }

    // Display Now Playing Movies
    public void displayNowPlaying() {
        // Instantiate the fragment.
        NowPlayingFragment nowPlayingFragment = NowPlayingFragment.newInstance();

        switchFragment(nowPlayingFragment, true, true);

        // Set boolean flag to indicate fragment is open.
        isNowPlayingFragmentDisplayed = true;
        isHomeFragmentDisplayed = false;
    }


    // Display Home Screen
    public void displayHome() {
        // Instantiate the fragment.
        HomeFragment homeFragment = HomeFragment.newInstance();

        switchFragment(homeFragment, true, false);

        // Set boolean flag to indicate fragment is open.
        isHomeFragmentDisplayed = true;
        isNowPlayingFragmentDisplayed = false;
    }

    public void switchFragment(Fragment fragment, boolean isToAddToBackStack, boolean isAnimationToLeft) {

        // Get the FragmentManager and start a transaction.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Set transition animations
        if (isAnimationToLeft) {
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        } else {
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_left);
        }



        // Replace the fragment
        fragmentTransaction.replace(R.id.fragment_container,
                fragment);
        if (isToAddToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    // TODO
    private void displaySearch() {


        // Instantiate the fragment.
        final SearchFragment searchFragment = SearchFragment.newInstance();

        switchFragment(searchFragment, true, false);




        /*Hide button when keyboard is open*/
        searchFragment.getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                searchFragment.getView().getWindowVisibleDisplayFrame(r);

                int heightDiff = searchFragment.getView().getRootView().getHeight() - (r.bottom - r.top);

                if (heightDiff > 244) { // if more than 100 pixels, its probably a keyboard...
                    //ok now we know the keyboard is up...
                    mBottomNavigationView.setVisibility(View.GONE);


                } else {
                    //ok now we know the keyboard is down...
                    mBottomNavigationView.setVisibility(View.VISIBLE);


                }
            }
        });

        // Set boolean flag to indicate fragment is open.
        isHomeFragmentDisplayed = true;
        isNowPlayingFragmentDisplayed = false;
    }


    /*@Override
    public void onBackPressed() {




            super.onBackPressed();
            mBottomNavigationView.setVisibility(View.VISIBLE);


    }*/




    @Override
    public void checkKeyboardFocus(boolean isKeyboardOpen) {
        if (isKeyboardOpen) {
            return;
        } else {
            //mSearch.setFocusable(false);
            return;
           // mBottomNavigationView.setVisibility(View.VISIBLE);
        }
        /*if (isSearchFocused == true){
            mBottomNavigationView.setVisibility(View.VISIBLE);
            //onBackPressed();
        }*/

    }
}
