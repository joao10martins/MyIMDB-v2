package com.example.myimdb;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.myimdb.helpers.SharedPreferencesHelper;
import com.example.myimdb.model.FavoritesRealm;
import com.example.myimdb.model.MovieDetails;
import com.example.myimdb.model.MovieRealm;
import com.example.myimdb.model.SearchMovieRealm;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class FavoritesFragment extends Fragment implements FavoritesRecyclerAdapter.OnMovieClick, SearchFragment.CheckKeyboardState {

    /* Views */
    private View mView;
    private RecyclerView mRecyclerView;
    private ImageView mPosterpathImage;
    private ImageView mLike;
    private TextView mOriginalTitle;
    private TextView mRating;

    /* Variables */
    private boolean isMovieViewAsList = false;
    private String mUrl;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private FavoritesRecyclerAdapter mAdapter;
    private OnFavoritesListener mListener;
    private boolean isFavoritesFromSearch;
    private boolean isFilterByName = false;
    private boolean isFilterByReleaseDate = false;
    private boolean isFilterByRating = false;
    private CheckBox nameCheckbox;
    private CheckBox dateCheckbox;
    private CheckBox ratingCheckbox;

    /* Realm */
    Realm mRealm;

    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_favorites, container, false);

        SharedPreferencesHelper prefs = SharedPreferencesHelper.getInstance();
        isMovieViewAsList = Boolean.valueOf(prefs.getPreferences("fav_viewMode", "false"));

        /* Setting the Filter Mode if there is one */
        isFilterByName = Boolean.valueOf(prefs.getPreferences("fav_filterByName", "false"));
        isFilterByReleaseDate = Boolean.valueOf(prefs.getPreferences("fav_filterByReleaseDate", "false"));
        isFilterByRating = Boolean.valueOf(prefs.getPreferences("fav_filterByRating", "false"));


        mRealm = Realm.getDefaultInstance();
        /*mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<FavoritesRealm> rows = realm.where(FavoritesRealm.class).findAll();
                rows.deleteAllFromRealm();
            }
        });*/
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());


        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mContext = getActivity();
        mRecyclerView = mView.findViewById(R.id.rv_Favorites);

        /*mPosterpathImage = mView.findViewById(R.id.fav_grid_layout_img);
        mLike = mView.findViewById(R.id.favorites_like);
        mOriginalTitle = mView.findViewById(R.id.fav_grid_layout_title);
        mRating = mView.findViewById(R.id.fav_grid_layout_rating);*/

        getFavorites();
        /* Setting the Filter Mode if there is one */
        if (isFilterByName && mAdapter != null){
            RealmResults<FavoritesRealm> filteredByName = mAdapter.getData().sort("title", Sort.ASCENDING);
            mAdapter.updateData(filteredByName);
            mRecyclerView.setAdapter(mAdapter);
        }
        if (isFilterByReleaseDate && mAdapter != null){
            RealmResults<FavoritesRealm> filteredByReleaseDate = mAdapter.getData().sort("release_date", Sort.DESCENDING);
            mAdapter.updateData(filteredByReleaseDate);
            mRecyclerView.setAdapter(mAdapter);
        }
        if (isFilterByRating && mAdapter != null) {
            RealmResults<FavoritesRealm> filteredByRating = mAdapter.getData().sort("vote_average", Sort.DESCENDING);
            mAdapter.updateData(filteredByRating);
            mRecyclerView.setAdapter(mAdapter);
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FavoritesFragment.OnFavoritesListener) {
            //init the listener
            mListener = (FavoritesFragment.OnFavoritesListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InteractionListener");
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar toolbar = ((MainActivity)getActivity()).getSupportActionBar();
        toolbar.setTitle("Favorites");
        menu.findItem(R.id.toolbar_visualization).setVisible(true);
        menu.findItem(R.id.toolbar_favorites).setVisible(false);
        menu.findItem(R.id.toolbar_sort).setVisible(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // TODO: switch case
        switch (id){
            case R.id.toolbar_favorites:
                //switchFragment();
                //return true;
            case R.id.toolbar_visualization:
                isMovieViewAsList = !isMovieViewAsList;

                int scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                // change between List and Grid layout(default: Grid)
                mAdapter = new FavoritesRecyclerAdapter(mRealm.where(FavoritesRealm.class).findAllAsync(), getContext(), FavoritesFragment.this, isMovieViewAsList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                if(mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    mRecyclerView.setLayoutManager(isMovieViewAsList ? new LinearLayoutManager(getContext()) : new GridLayoutManager(getContext(), 2));
                } else {
                    mRecyclerView.setLayoutManager(isMovieViewAsList ? new LinearLayoutManager(getContext()) : new GridLayoutManager(getContext(), 2));
                }
                mRecyclerView.scrollToPosition(scrollPosition);


                //return true;
            case R.id.toolbar_sort:
                //custom popup
                final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_sort_by_alert_dialog, (ViewGroup) getView().getRootView(), false);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setCancelable(true);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                /* Filter by NAME */
                nameCheckbox = dialogView.findViewById(R.id.filterByName_checkbox);
                nameCheckbox.setChecked(isFilterByName);
                nameCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        isFilterByName = !isFilterByName;
                        if (isFilterByRating || isFilterByReleaseDate){
                            isFilterByRating = false;
                            ratingCheckbox.setChecked(isFilterByRating);
                            isFilterByReleaseDate = false;
                            dateCheckbox.setChecked(isFilterByReleaseDate);
                        }
                        if (isFilterByName && mAdapter != null){
                            RealmResults<FavoritesRealm> filteredByName = mAdapter.getData().sort("title", Sort.ASCENDING);
                            mAdapter.updateData(filteredByName);
                            mRecyclerView.setAdapter(mAdapter);
                            alertDialog.dismiss();
                        } else {
                            // remove filter
                            RealmResults<FavoritesRealm> unfilteredResults = mRealm.where(FavoritesRealm.class)
                                    .findAllAsync();
                            mAdapter.updateData(unfilteredResults);
                            mRecyclerView.setAdapter(mAdapter);
                            alertDialog.dismiss();
                        }
                    }
                });

                /* Filter by RELEASE DATE */
                dateCheckbox = dialogView.findViewById(R.id.filterByDate_checkbox);
                dateCheckbox.setChecked(isFilterByReleaseDate);
                dateCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        isFilterByReleaseDate = !isFilterByReleaseDate;
                        if (isFilterByRating || isFilterByName){
                            isFilterByRating = false;
                            ratingCheckbox.setChecked(isFilterByRating);
                            isFilterByName = false;
                            nameCheckbox.setChecked(isFilterByName);
                        }
                        if (isFilterByReleaseDate && mAdapter != null){
                            RealmResults<FavoritesRealm> filteredByReleaseDate = mAdapter.getData().sort("release_date", Sort.DESCENDING);
                            mAdapter.updateData(filteredByReleaseDate);
                            mRecyclerView.setAdapter(mAdapter);
                            alertDialog.dismiss();
                        } else {
                            // remove filter
                            RealmResults<FavoritesRealm> unfilteredResults = mRealm.where(FavoritesRealm.class)
                                    .findAllAsync();
                            mAdapter.updateData(unfilteredResults);
                            mRecyclerView.setAdapter(mAdapter);
                            alertDialog.dismiss();
                        }
                    }
                });

                /* Filter by RATING */
                ratingCheckbox = dialogView.findViewById(R.id.filterByRating_checkbox);
                ratingCheckbox.setChecked(isFilterByRating);
                ratingCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        isFilterByRating = !isFilterByRating;
                        if (isFilterByName || isFilterByReleaseDate){
                            isFilterByName = false;
                            nameCheckbox.setChecked(isFilterByName);
                            isFilterByReleaseDate = false;
                            dateCheckbox.setChecked(isFilterByReleaseDate);
                        }
                        if (isFilterByRating && mAdapter != null){
                            RealmResults<FavoritesRealm> filteredByRating = mAdapter.getData().sort("vote_average", Sort.DESCENDING);
                            mAdapter.updateData(filteredByRating);
                            mRecyclerView.setAdapter(mAdapter);
                            alertDialog.dismiss();
                        } else {
                            // remove filter
                            RealmResults<FavoritesRealm> unfilteredResults = mRealm.where(FavoritesRealm.class)
                                    .findAllAsync();
                            mAdapter.updateData(unfilteredResults);
                            mRecyclerView.setAdapter(mAdapter);
                            alertDialog.dismiss();
                        }
                    }
                });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }



    private void getFavorites() {
        if (mRecyclerView != null && mRealm.where(MovieRealm.class).findAllAsync() != null) { // Executes in case data already exists, to avoid making unnecessary requests to the API
            mAdapter = new FavoritesRecyclerAdapter(mRealm.where(FavoritesRealm.class).findAllAsync(), getContext(), FavoritesFragment.this, isMovieViewAsList);
            //mAdapter = new NowPlayingRecyclerAdapter(getContext(), nowPlayingList, NowPlayingFragment.this);
            mRecyclerView.setAdapter(mAdapter);

            mRecyclerView.setLayoutManager(isMovieViewAsList ? new LinearLayoutManager(getContext()) : new GridLayoutManager(getContext(), 2));
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }



    @Override
    public void onItemClick(int movieId, String title) {

        mUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US";

        GsonRequest<MovieDetails> request = new GsonRequest<>(mUrl,
                MovieDetails.class,
                getDetailsSuccessListener(),
                getErrorListener());

        mRequestQueue.add(request);
        mListener.onDetailClick(title);
    }




    private Response.Listener<MovieDetails> getDetailsSuccessListener() {
        return new Response.Listener<MovieDetails>() {
            @Override
            public void onResponse(MovieDetails response) {



                // Pack the response data in a Bundle.
                Bundle bundle = new Bundle();
                bundle.putInt("id", response.getId());
                bundle.putString("original_title", response.getOriginal_title());
                bundle.putString("backdrop_path", response.getBackdrop_path());
                bundle.putString("overview", response.getOverview());
                bundle.putString("poster_path", response.getPoster_path());
                bundle.putString("release_date", response.getRelease_date());
                bundle.putInt("runtime", response.getRuntime());
                bundle.putDouble("vote_average", response.getVote_average());
                bundle.putInt("vote_count", response.getVote_count());
                bundle.putDouble("popularity", response.getPopularity());



                // Send the response data stored within the Bundle to the Fragment.
                DetailsFragment detailsFragment = DetailsFragment.newInstance();
                detailsFragment.setArguments(bundle);



                // Send detailsFragment to Main
                //mListener.onSwitchFragment(detailsFragment);

                // Replace fragment after work is done.
                // Get the FragmentManager and start a transaction.
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();



                // Replace the fragment
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.replace(R.id.fragment_container,
                        detailsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        };
    }

    private Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do whatever you want to do with error.getMessage();
                error.printStackTrace();
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Favorites");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferencesHelper.getInstance().setPreferences("fav_viewMode", String.valueOf(isMovieViewAsList));
        /* Save filters */
        SharedPreferencesHelper.getInstance().setPreferences("fav_filterByName", String.valueOf(isFilterByName));
        SharedPreferencesHelper.getInstance().setPreferences("fav_filterByReleaseDate", String.valueOf(isFilterByReleaseDate));
        SharedPreferencesHelper.getInstance().setPreferences("fav_filterByRating", String.valueOf(isFilterByRating));
    }


    @Override
    public void onKeyboardStateChanged(boolean isOpen) {

    }

    @Override
    public void onDetailClick(String title) {

    }

    @Override
    public void isFromSearch(boolean isFromSearch) {
        //isFavoritesFromSearch = isFromSearch;
    }

    @Override
    public void onFavoritesClick(boolean favoritesFromSearch) {
        //isFavoritesFromSearch = favoritesFromSearch;
    }

    @Override
    public int getBackCount() {
        return 0;
    }


    public interface OnFavoritesListener {
        void onDetailClick(String title);
    }


}
