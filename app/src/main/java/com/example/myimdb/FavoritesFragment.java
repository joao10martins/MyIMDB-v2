package com.example.myimdb;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.myimdb.model.FavoritesRealm;
import com.example.myimdb.model.MovieDetails;
import com.example.myimdb.model.MovieRealm;

import io.realm.Realm;

public class FavoritesFragment extends Fragment implements FavoritesRecyclerAdapter.OnMovieClick {

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


        mRealm = Realm.getDefaultInstance();


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

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.toolbar_visualization).setVisible(true);
        menu.findItem(R.id.toolbar_favorites).setVisible(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // TODO: switch case
        switch (id){
            case R.id.toolbar_favorites:
                //switchFragment();
                return true;
            case R.id.toolbar_visualization:
                /*isMovieViewAsList = !isMovieViewAsList;

                int scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                // change between List and Grid layout(default: Grid)
                mAdapter = new NowPlayingRecyclerAdapter(mRealm.where(FavoritesRealm.class).findAllAsync(), getContext(), FavoritesFragment.this, isMovieViewAsList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

                if(mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    mRecyclerView.setLayoutManager(isMovieViewAsList ? new LinearLayoutManager(getContext()) : new GridLayoutManager(getContext(), 3));
                } else {
                    mRecyclerView.setLayoutManager(isMovieViewAsList ? new LinearLayoutManager(getContext()) : new GridLayoutManager(getContext(), 3));
                }
                mRecyclerView.scrollToPosition(scrollPosition);*/


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
        // TODO: send title by interface listener to MainActivity.
        mUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US";

        GsonRequest<MovieDetails> request = new GsonRequest<>(mUrl,
                MovieDetails.class,
                getDetailsSuccessListener(),
                getErrorListener());

        mRequestQueue.add(request);
        mListener.onDetailClick(title); // TODO: adapter interface listener
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



    public interface OnFavoritesListener {
        void onDetailClick(String title);
    }
}
