package com.example.myimdb;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private View mView;
    ConstraintLayout constraintLayout;
    AnimationDrawable animDrawable;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);


        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar toolbar = ((MainActivity)getActivity()).getSupportActionBar();
        toolbar.setTitle("Home");
        menu.findItem(R.id.toolbar_visualization).setVisible(false);
        menu.findItem(R.id.toolbar_favorites).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // TODO: switch case
        switch (id){
            case R.id.toolbar_favorites:
                //switchFragment();
                // Get the FragmentManager and start a transaction.
                FavoritesFragment favoritesFragment = FavoritesFragment.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                // Replace the fragment
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.fragment_container,
                        favoritesFragment);
                fragmentTransaction.addToBackStack(null);
                ActionBar toolbar = ((MainActivity)getActivity()).getSupportActionBar();
                toolbar.setTitle("Favorites");
                fragmentTransaction.commit();
                //return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Home");
        }
    }
}
