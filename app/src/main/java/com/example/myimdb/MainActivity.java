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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myimdb.model.Movie;
import com.example.myimdb.model.MovieRealm;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements SearchFragment.CheckKeyboardState, NowPlayingFragment.OnNowPlayingListener {

    /* Constants */
    private static final String TAG = MainActivity.class.getCanonicalName();

    /* Properties */
    private View mView;

    /* Variables */
    private boolean isNowPlayingFragmentDisplayed = false;
    private boolean isHomeFragmentDisplayed = false;
    private boolean isSearchFragmentDisplayed = false;
    private boolean isDetailsFragmentDisplayed = false;

    /* Realm */
    Realm mRealm;


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
                    if (isNowPlayingFragmentDisplayed  || isSearchFragmentDisplayed){
                        displayHome();
                    }
                    return true;
                case R.id.navigation_nowplaying:
                    if (isNowPlayingFragmentDisplayed){
                        return true;
                    }
                    if (isHomeFragmentDisplayed || isSearchFragmentDisplayed || isDetailsFragmentDisplayed){
                        displayNowPlaying();

                    }
                    return true;
                case R.id.navigation_search:
                    if (isSearchFragmentDisplayed){
                        return true;
                    }
                    if (isHomeFragmentDisplayed || isNowPlayingFragmentDisplayed || isDetailsFragmentDisplayed){
                        displaySearch();
                    }
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

        //mRealm = Realm.getDefaultInstance();
        /*new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();*/



        mBottomNavigationView = findViewById(R.id.navigation);
        mBottomNavigationView.setSelectedItemId(R.id.navigation_home);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


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
        isSearchFragmentDisplayed = false;

    }


    // Display Home Screen
    public void displayHome() {
        // Instantiate the fragment.
        HomeFragment homeFragment = HomeFragment.newInstance();

        if (isNowPlayingFragmentDisplayed){
            switchFragment(homeFragment, false, false);
            // Set boolean flag to indicate fragment is open.
            isHomeFragmentDisplayed = true;
            isNowPlayingFragmentDisplayed = false;
            isSearchFragmentDisplayed = false;
        }
        if (isSearchFragmentDisplayed){
            switchFragment(homeFragment, false, true);
            // Set boolean flag to indicate fragment is open.
            isHomeFragmentDisplayed = true;
            isNowPlayingFragmentDisplayed = false;
            isSearchFragmentDisplayed = false;
        }



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



        // Add test
        /*if(!isHomeFragmentDisplayed && isNowPlayingFragmentDisplayed){
            fragmentTransaction.add(R.id.fragment_container, fragment);
        } else {*/
            // Replace the fragment
            //fragmentManager.saveFragmentInstanceState(fragment);

            fragmentTransaction.replace(R.id.fragment_container, fragment);
            //fragmentTransaction.add(R.id.fragment_container, fragment);
        //}


        if (isToAddToBackStack) {
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.commit();
        }
    }


    // TODO
    private void displaySearch() {


        // Instantiate the fragment.
        final SearchFragment searchFragment = SearchFragment.newInstance();

        switchFragment(searchFragment, true, false);






        // Set boolean flag to indicate fragment is open.
        isHomeFragmentDisplayed = false;
        isNowPlayingFragmentDisplayed = false;
        isSearchFragmentDisplayed = true;
        isDetailsFragmentDisplayed = false;
    }




    /* Interface listener implementation */

    @Override
    public void onKeyboardStateChanged(boolean isOpen) {

        mBottomNavigationView.setVisibility(isOpen ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onSwitchFragment(Fragment fragment) {
        switchFragment(fragment, true, false);
    }
}
