package com.example.myimdb;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.myimdb.map.MovieMapper;
import com.example.myimdb.model.Movie;
import com.example.myimdb.model.MovieDetails;
import com.example.myimdb.model.MovieRealm;
import com.example.myimdb.model.MovieResults;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment implements NowPlayingRecyclerAdapter.OnMovieClick {
    private OnNowPlayingListener mListener;
    private List<Movie> nowPlayingList = new ArrayList<>();
    private Context mContext;

    private RecyclerView mRecyclerView;
    private NowPlayingRecyclerAdapter mAdapter;
    private int mListCount;
    private RequestQueue mRequestQueue;
    private String mUrl;
    private int mTotalPages;

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


        mRealm = Realm.getDefaultInstance();
        /*mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<MovieRealm> rows = realm.where(MovieRealm.class).findAll();
                rows.deleteAllFromRealm();
            }
        });*/

        //final RealmResults<MovieRealm> movies = mRealm.where(MovieRealm.class).findAll();
        //System.out.println("༼ つ ◕_◕ ༽つ THE MOVIES ARE HERE, COME GET'EM ༼ つ ◕_◕ ༽つ" + movies);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();

        mRecyclerView = mView.findViewById(R.id.rv_NowPlaying);

        getNowPlaying();
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

        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        mListCount = 1;

        try {
            if (nowPlayingList.size() == 0 || mRealm.where(MovieRealm.class).findAllAsync() == null) { // Executes if it is being called for the first time(has no data yet)
                mUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US&page=1";

                GsonRequest<MovieResults> request = new GsonRequest<>(mUrl,
                        MovieResults.class,
                        createMyReqSuccessListener(),
                        getErrorListener());

                mRequestQueue.add(request);
                ++mListCount;
            }

            if (mRecyclerView != null && mRealm.where(MovieRealm.class).findAllAsync() != null) { // Executes in case data already exists, to avoid making unnecessary requests to the API
                mAdapter = new NowPlayingRecyclerAdapter(mRealm.where(MovieRealm.class).findAllAsync(), getContext(), NowPlayingFragment.this);
                //mAdapter = new NowPlayingRecyclerAdapter(getContext(), nowPlayingList, NowPlayingFragment.this);
                mRecyclerView.setAdapter(mAdapter);

                mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
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

                                GsonRequest<MovieResults> request = new GsonRequest<>(mUrl,
                                        MovieResults.class,
                                        createMyReqSuccessListener(),
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


    private Response.Listener<MovieResults> createMyReqSuccessListener() {
        return new Response.Listener<MovieResults>() {
            @Override
            public void onResponse(MovieResults response) {
                try {
                    mTotalPages = response.totalPages;
                    nowPlayingList.addAll(response.movieList);
                    saveMovieListToDb(nowPlayingList); // PLEASE WORK ༼ つ ◕_◕ ༽つ
                    if (mAdapter == null) {
                        mAdapter = new NowPlayingRecyclerAdapter(mRealm.where(MovieRealm.class).findAllAsync(), getContext(), NowPlayingFragment.this); // test
                        //mAdapter = new NowPlayingRecyclerAdapter(getContext(), nowPlayingList, NowPlayingFragment.this);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
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

                                    GsonRequest<MovieResults> request = new GsonRequest<>(mUrl,
                                            MovieResults.class,
                                            createMyReqSuccessListener(),
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
    public void onItemClick(int movieId) {
        mUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US";

        GsonRequest<MovieDetails> request = new GsonRequest<>(mUrl,
                MovieDetails.class,
                getDetailsSuccessListener(),
                getErrorListener());

        mRequestQueue.add(request);
    }

    private Response.Listener<MovieDetails> getDetailsSuccessListener() {
        return new Response.Listener<MovieDetails>() {
            @Override
            public void onResponse(MovieDetails response) {
                // TODO: send response data to Details fragment
                // and replace fragment with details fragment

                // Pack the response data in a Bundle.
                Bundle bundle = new Bundle();
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

    // ༼ つ ◕_◕ ༽つ PLS WORK WITHOUT PROBLEMS ༼ つ ◕_◕ ༽つ
    private void saveMovieListToDb(final List<Movie> list){
        // TEST
        final MovieMapper movieMapper = new MovieMapper();

        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    List<MovieRealm> movies = movieMapper.toMovieRealmList(list);
                    RealmList<MovieRealm> _movies = new RealmList<>();
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


        // Query Realm for all movies
        //final RealmResults<MovieRealm> movies = mRealm.where(MovieRealm.class).findAll();

        // Realm transaction (hopefully saves data persistently)
        /*mRealm.beginTransaction();
        mRealm.insertOrUpdate(movies);
        mRealm.commitTransaction();*/

        // Listeners will be notified when data changes
        /*movies.addChangeListener(new OrderedRealmCollectionChangeListener<RealmList<MovieRealm>>() {
            @Override
            public void onChange(RealmResults<MovieRealm> movies, OrderedCollectionChangeSet changeSet) {
                // Query results are updated in real time with fine grained notifications.
                changeSet.getInsertions();
            }
        });*/


    public interface OnNowPlayingListener {
        void onSwitchFragment(Fragment fragment);
    }


}








