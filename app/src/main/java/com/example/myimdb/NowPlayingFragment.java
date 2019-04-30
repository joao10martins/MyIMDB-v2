package com.example.myimdb;



import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.myimdb.adapters.NowPlayingRecyclerAdapter;
import com.example.myimdb.adapters.PopularRecyclerAdapter;
import com.example.myimdb.helpers.GsonRequest;
import com.example.myimdb.helpers.SharedPreferencesHelper;
import com.example.myimdb.map.MovieMapper;
import com.example.myimdb.model.realm.PopularRealm;
import com.example.myimdb.model.response.Movie;
import com.example.myimdb.model.response.MovieDetails;
import com.example.myimdb.model.realm.MovieRealm;
import com.example.myimdb.model.response.MovieResults;
import com.example.myimdb.model.response.Popular;
import com.example.myimdb.model.response.PopularResults;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment implements PopularRecyclerAdapter.OnMovieClick {
    private OnNowPlayingListener mListener;
    private List<Popular> popularList = new ArrayList<>();
    private Context mContext;

    private RecyclerView mRecyclerView;
    private PopularRecyclerAdapter mAdapter;
    private int mListCount;
    private RequestQueue mRequestQueue;
    private String mUrl;
    private int mTotalPages;
    private boolean isMovieViewAsList = false;
    private int mScrollPosition;
    private TabLayout mNowPlayingTabs;

    private View mView;


    /* Realm */
    Realm mRealm;

    public NowPlayingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_now_playing, container, false);


        SharedPreferencesHelper prefs = SharedPreferencesHelper.getInstance();
        isMovieViewAsList = Boolean.valueOf(prefs.getPreferences("np_viewMode", "false"));









        mRealm = Realm.getDefaultInstance();
        /*mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<MovieRealm> rows = realm.where(MovieRealm.class).findAll();
                rows.deleteAllFromRealm();
            }
        });*/


        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mContext = getActivity();

        mRecyclerView = mView.findViewById(R.id.rv_NowPlaying);
        mNowPlayingTabs = mView.findViewById(R.id.tab_layout_menu);


        getNowPlaying();
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar toolbar = ((MainActivity)getActivity()).getSupportActionBar();
        toolbar.setTitle("Now Playing");
        menu.findItem(R.id.toolbar_visualization).setVisible(true);
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
                return true;
            case R.id.toolbar_visualization:
                isMovieViewAsList = !isMovieViewAsList;

                mScrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                /* Change between List and Grid layout(default: Grid) */
                /*TODO: check type of RecyclerAdapter(booleans) and change viewMode accordingly(might not be needed)
                  maybe having 3 adapters(mPopular, mTopRated, mUpcoming) is a better approach than creating a new Adapter everytime. */
                mAdapter = new PopularRecyclerAdapter(mRealm.where(PopularRealm.class).findAllAsync(), getContext(), NowPlayingFragment.this, isMovieViewAsList);
                mRecyclerView.setAdapter(mAdapter);
                rvItemAnim();
                mAdapter.notifyDataSetChanged();

                if(mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    mRecyclerView.setLayoutManager(isMovieViewAsList ? new LinearLayoutManager(getContext()) : new GridLayoutManager(getContext(), 3));
                } else {
                    mRecyclerView.setLayoutManager(isMovieViewAsList ? new LinearLayoutManager(getContext()) : new GridLayoutManager(getContext(), 3));
                }
                mRecyclerView.scrollToPosition(mScrollPosition);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NowPlayingFragment.OnNowPlayingListener) {
            //init the listener
            mListener = (NowPlayingFragment.OnNowPlayingListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InteractionListener");
        }
    }



    public static NowPlayingFragment newInstance() {
        return new NowPlayingFragment();
    }


    private void getNowPlaying(){

        mNowPlayingTabs.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrenTabDisplay(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        mListCount = 1;

        try {
            if (popularList.size() == 0 || mRealm.where(PopularRealm.class).findAllAsync() == null) { // Executes if it is being called for the first time(has no data yet)
                mUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US&page=1";

                GsonRequest<PopularResults> request = new GsonRequest<>(mUrl,
                        PopularResults.class,
                        getPopularSuccessListener(),
                        getErrorListener());

                mRequestQueue.add(request);
                ++mListCount;
            }

            if (mRecyclerView != null && mRealm.where(PopularRealm.class).findAllAsync() != null) { // Executes in case data already exists, to avoid making unnecessary requests to the API
                mAdapter = new PopularRecyclerAdapter(mRealm.where(PopularRealm.class).findAllAsync(), getContext(), NowPlayingFragment.this, isMovieViewAsList);
                //mAdapter = new NowPlayingRecyclerAdapter(getContext(), nowPlayingList, NowPlayingFragment.this);
                mRecyclerView.setAdapter(mAdapter);
                rvItemAnim();
                mRecyclerView.setLayoutManager(isMovieViewAsList ? new LinearLayoutManager(getContext()) : new GridLayoutManager(getContext(), 3));
            } else {
                mAdapter.notifyDataSetChanged();
            }

                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        if (!recyclerView.canScrollVertically(1)) {
                            //do something
                            if (mListCount > mTotalPages) {
                                return;
                            } else {
                                mUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US&page=" + mListCount;

                                GsonRequest<PopularResults> request = new GsonRequest<>(mUrl,
                                        PopularResults.class,
                                        getPopularSuccessListener(),
                                        getErrorListener());

                                mRequestQueue.add(request);
                                ++mListCount;
                            }
                        }
                    }
                });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setCurrenTabDisplay(int tabPosition) {
        switch (tabPosition) {
            case 0 :
                //display popular
                break;
            case 1 :
                //display top rated
                break;
            case 2:
                //display upcoming
                break;
        }
    }


    private Response.Listener<PopularResults> getPopularSuccessListener() {
        return new Response.Listener<PopularResults>() {
            @Override
            public void onResponse(PopularResults response) {
                try {
                    mTotalPages = response.totalPages;
                    popularList.addAll(response.popularMovieList);
                    savePopularMovieListToDb(popularList); // PLEASE WORK ༼ つ ◕_◕ ༽つ
                    if (mAdapter == null) {
                        mAdapter = new NowPlayingRecyclerAdapter(mRealm.where(MovieRealm.class).findAllAsync(), getContext(), NowPlayingFragment.this, isMovieViewAsList);
                        //mAdapter = new NowPlayingRecyclerAdapter(getContext(), nowPlayingList, NowPlayingFragment.this);
                        mRecyclerView.setAdapter(mAdapter);
                        rvItemAnim();
                        mRecyclerView.setLayoutManager(isMovieViewAsList ? new LinearLayoutManager(getContext()) : new GridLayoutManager(getContext(), 3));
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }



                    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);

                            if (!recyclerView.canScrollVertically(1)) {
                                //do something
                                if (mListCount > mTotalPages) {
                                    return;
                                } else {
                                    mUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US&page=" + mListCount;

                                    GsonRequest<PopularResults> request = new GsonRequest<>(mUrl,
                                            PopularResults.class,
                                            getPopularSuccessListener(),
                                            getErrorListener());

                                    mRequestQueue.add(request);
                                    ++mListCount;
                                }
                            }
                        }
                    });

                    //mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    public void onItemClick(int movieId, String title) {

        mUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US";

        GsonRequest<MovieDetails> request = new GsonRequest<>(mUrl,
                MovieDetails.class,
                getDetailsSuccessListener(),
                getErrorListener());

        mRequestQueue.add(request);
        mListener.onDetailClick(title); // test
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

    private void rvItemAnim() {

        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                for (int i=0; i < mRecyclerView.getChildCount(); i++) {
                    View v = mRecyclerView.getChildAt(i);
                    v.setAlpha(0.0f);
                    v.animate().alpha(1.0f)
                            .setDuration(700)
                            .setStartDelay(i * 50)
                            .start();
                }
                return true;
            }
        });
    }

    // ༼ つ ◕_◕ ༽つ PLS WORK WITHOUT PROBLEMS ༼ つ ◕_◕ ༽つ
    private void savePopularMovieListToDb(final List<Popular> list){
        // TEST
        final MovieMapper movieMapper = new MovieMapper();

        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    List<PopularRealm> movies = movieMapper.toPopularRealmList(list);
                    RealmList<PopularRealm> _movies = new RealmList<>();
                    _movies.addAll(movies);
                    realm.insertOrUpdate(_movies);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    // GREAT SUCCESS (•̀ᴗ•́)و ̑̑
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    // sad reactions only
                }
            });
        } catch (Exception e) {
            // Wow such exception
            e.printStackTrace();
        } finally {
            // Wow such finally
        }
    }


    public interface OnNowPlayingListener {
        void onSwitchFragment(Fragment fragment);
        void onDetailClick(String title);
    }


    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Now Playing");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferencesHelper.getInstance().setPreferences("np_viewMode", String.valueOf(isMovieViewAsList));
        //SharedPreferencesHelper.getInstance().setPreferences("np_scrollPos", String.valueOf(mScrollPosition));
    }
}








