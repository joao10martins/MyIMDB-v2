package com.example.myimdb;


import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.myimdb.map.GenreMapper;
import com.example.myimdb.map.MovieMapper;
import com.example.myimdb.model.Movie;
import com.example.myimdb.model.MovieDetails;
import com.example.myimdb.model.MovieGenre;
import com.example.myimdb.model.MovieGenreRealm;
import com.example.myimdb.model.MovieGenreResults;
import com.example.myimdb.model.MovieRealm;
import com.example.myimdb.model.MovieResults;
import com.example.myimdb.model.SearchMovie;
import com.example.myimdb.model.SearchMovieRealm;
import com.example.myimdb.model.SearchMovieResults;


import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements SearchRecyclerAdapter.OnMovieClick{

    private View mView;
    private Context mContext;
    private Unregistrar mUnregistrar;


    private RequestQueue mRequestQueue;
    private String mUrl;
    private RecyclerView mRecyclerView;

    //private List<MovieGenre> mGenreList = new ArrayList<>();
    private HashMap<Integer , MovieGenreRealm> mGenreMap = new HashMap<>();
    private List<MovieGenre> genreList = new ArrayList<>();
    private SearchRecyclerAdapter mAdapter;
    private int mListCount;
    private String mSearch_query;
    private int mTotalPages;
    RealmResults<SearchMovieRealm> mResults;

    private CheckKeyboardState mListener;

    /* Realm */
    Realm mRealm;


    private List<SearchMovie> mMovieList = new ArrayList<>();
    private int mButtonClickCount;


    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CheckKeyboardState) {
            //init the listener
            mListener = (CheckKeyboardState) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement InteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_search, container, false);

        // get db
        mRealm = Realm.getDefaultInstance();



        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();
        mRecyclerView = mView.findViewById(R.id.rvSearch);


        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<SearchMovieRealm> rows = realm.where(SearchMovieRealm.class).findAll();
                rows.deleteAllFromRealm();
            }
        });

        registerKeyboardListener();

        // Initialize the list of Genres.
        getGenreList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mUnregistrar.unregister();
    }

    // Get List of all the existing Genres in the API.
    private void getGenreList() {
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        try {
            // If there is no Genres data on Realm, try to fetch it from the API, if internet connection is available.
            if (mGenreMap.isEmpty() && mRealm.where(MovieGenreRealm.class).findAllAsync() == null) {
                if (isConnectedToNetwork()) {
                    mUrl = "https://api.themoviedb.org/3/genre/movie/list?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US";

                    GsonRequest<MovieGenreResults> request = new GsonRequest<>(mUrl,
                            MovieGenreResults.class,
                            getGenreSuccessListener(),
                            getErrorListener());

                    mRequestQueue.add(request);
                } else {
                    //Show disconnected message
                    Toast.makeText(getContext(), "Network connection unavailable", Toast.LENGTH_SHORT).show();
                }
            // There is Genres data on Realm, so we will query Realm instead.
            } else {
                for (MovieGenreRealm movieGenre : mRealm.where(MovieGenreRealm.class).findAllAsync()) {
                    mGenreMap.put(movieGenre.getId(), movieGenre);
                }
                searchQuery();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private Response.Listener<MovieGenreResults> getGenreSuccessListener() {
        return new Response.Listener<MovieGenreResults>() {
            @Override
            public void onResponse(MovieGenreResults response) {
                genreList.addAll(response.genreList);
                saveGenresToDb(genreList);
                try {
                    for (MovieGenreRealm movieGenre : mRealm.where(MovieGenreRealm.class).findAllAsync()) {
                        mGenreMap.put(movieGenre.getId(), movieGenre);
                    }
                    //mGenreList.addAll(response.genreList);
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



    // Test query
    private void searchQuery() {
        mListCount = 1;
        mButtonClickCount = 1;
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        final EditText query_text = mView.findViewById(R.id.search_movie);


        ImageButton btnSearch = mView.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearch_query = query_text.getText().toString().trim();


                try {
                    if (mSearch_query.isEmpty() || mSearch_query.trim().isEmpty())
                        Toast.makeText(getActivity(), "Please enter a valid input", Toast.LENGTH_SHORT).show();
                    if ((mMovieList.size() == 0 || mRealm.where(SearchMovieRealm.class).findAllAsync() == null)  && mButtonClickCount == 1 && !mSearch_query.isEmpty() && !mSearch_query.trim().isEmpty()) {
                        // neste ponto, verificar conexão de internet e dependendo disso, fazer o request à API ou ao Realm.

                        if (isConnectedToNetwork()){
                            mUrl = "https://api.themoviedb.org/3/search/movie?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US&query=" + mSearch_query + "&page=1&include_adult=false";

                            GsonRequest<SearchMovieResults> request = new GsonRequest<>(mUrl,
                                    SearchMovieResults.class,
                                    getMovieSuccessListener(),
                                    getErrorListener());

                            mRequestQueue.add(request);
                            ++mListCount;
                            ++mButtonClickCount;
                        } else {
                            //Show disconnected message
                            Toast.makeText(getContext(), "Network connection unavailable", Toast.LENGTH_SHORT).show();
                        }

                    }

                    if(mAdapter != null && mButtonClickCount > 1){

                        if (isConnectedToNetwork()){
                            mUrl = "https://api.themoviedb.org/3/search/movie?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US&query=" + mSearch_query + "&page=1&include_adult=false";

                            GsonRequest<SearchMovieResults> request = new GsonRequest<>(mUrl,
                                    SearchMovieResults.class,
                                    getMovieSuccessListener(),
                                    getErrorListener());

                            mRequestQueue.add(request);
                            ++mListCount;
                        } else {
                            if (mResults != null){
                                mAdapter = new SearchRecyclerAdapter(getContext(), mResults, SearchFragment.this);
                                mRecyclerView.setAdapter(mAdapter);
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            } else {
                                //Show disconnected message
                                Toast.makeText(getContext(), "Network connection unavailable", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }


                    mResults = mRealm.where(SearchMovieRealm.class)
                            .like("title", "*"+mSearch_query.trim()+"*", Case.INSENSITIVE) // Finds any value that have 'mSearchQuery' in any position.
                            .findAllAsync();
                    if (mResults != null && mRecyclerView != null){
                        mAdapter = new SearchRecyclerAdapter(getContext(), mResults, SearchFragment.this);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                                    if (isConnectedToNetwork()) {
                                        mUrl = "https://api.themoviedb.org/3/search/movie?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US&query=" + mSearch_query + "&page=" + mListCount + "&include_adult=false";

                                        GsonRequest<SearchMovieResults> request = new GsonRequest<>(mUrl,
                                                SearchMovieResults.class,
                                                getMovieSuccessListener(),
                                                getErrorListener());

                                        mRequestQueue.add(request);
                                        ++mListCount;
                                    } else {
                                        //Show disconnected message
                                        Toast.makeText(getContext(), "Network connection unavailable", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }


    private Response.Listener<SearchMovieResults> getMovieSuccessListener() {
        return new Response.Listener<SearchMovieResults>() {
            @Override
            public void onResponse(SearchMovieResults response) {
                try {
                    if(response.searchMovieList.size() != 0) {
                        mTotalPages = response.totalPages;
                        mMovieList.addAll(response.searchMovieList);

                        for (SearchMovie movie : mMovieList) {
                            // iterar genres_ids da API
                            // Set genres description
                            movie.setGenresDescription("");
                            for (int genreId : movie.getGenre_ids()) {
                                movie.setGenresDescription(movie.getGenresDescription().concat(mGenreMap.get(genreId).getName()) + ", ");
                            }
                            // Remove white space if it exists.
                            if (movie.getGenresDescription().endsWith(" ")) {
                                movie.setGenresDescription(movie.getGenresDescription().substring(0, movie.getGenresDescription().length() - 1));
                            }
                            // Remove last comma, if it exists.
                            if (movie.getGenresDescription().endsWith(",")) {
                                movie.setGenresDescription(movie.getGenresDescription().substring(0, movie.getGenresDescription().length() - 1));
                            }
                        }
                        if (!mRealm.where(SearchMovieRealm.class).findAllAsync().containsAll(mMovieList)) {
                            saveSearchMovieListToDb(mMovieList);
                        }




                        if (mAdapter == null) {
                            mResults = mRealm.where(SearchMovieRealm.class)
                                    .like("title", "*"+mSearch_query.trim()+"*", Case.INSENSITIVE) // Finds any value that have 'mSearchQuery' in any position.
                                    .findAllAsync();

                            mAdapter = new SearchRecyclerAdapter(getContext(), mResults, SearchFragment.this);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
                                        if (isConnectedToNetwork()) {
                                            mUrl = "https://api.themoviedb.org/3/search/movie?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US&query=" + mSearch_query + "&page=" + mListCount + "&include_adult=false";

                                            GsonRequest<SearchMovieResults> request = new GsonRequest<>(mUrl,
                                                    SearchMovieResults.class,
                                                    getMovieSuccessListener(),
                                                    getErrorListener());

                                            mRequestQueue.add(request);
                                            ++mListCount;
                                        } else {
                                            //Show disconnected message
                                            Toast.makeText(getContext(), "Network connection unavailable", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        return;
                    }

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onItemClick(int movieId) {
        if (isConnectedToNetwork()) {
            mUrl = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=07d93ad59393a99fe6bc8c1b8f0de23b&language=en-US";

            GsonRequest<MovieDetails> request = new GsonRequest<>(mUrl,
                    MovieDetails.class,
                    getDetailsSuccessListener(),
                    getErrorListener());

            mRequestQueue.add(request);
        } else {
            //Show disconnected message
            Toast.makeText(getContext(), "Network connection unavailable", Toast.LENGTH_SHORT).show();
        }

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



                // Replace fragment after work is done.
                // Get the FragmentManager and start a transaction.
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();



                // Replace the fragment
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.add(R.id.fragment_container,
                        detailsFragment);
                fragmentTransaction.commit();
            }
        };
    }


    /* Save Genres to Realm DB */
    private void saveGenresToDb(final List<MovieGenre> list){
        final GenreMapper genreMapper = new GenreMapper();
        try(Realm realm = Realm.getDefaultInstance()){
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    List<MovieGenreRealm> genresList = genreMapper.toGenreRealmList(list);
                    //HashMap<Integer, MovieGenreRealm> genresMap = genreMapper.toGenreRealmMap(genresList); // Map is not supported by Realm.
                    RealmList<MovieGenreRealm> _genresList = new RealmList<>();
                    _genresList.addAll(genresList);
                    realm.insertOrUpdate(_genresList);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    // GREAT SUCCESS (•̀ᴗ•́)و ̑̑
                    // Search movie
                    searchQuery();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    // sad reactions only
                }
            });
        } catch (Exception e){
            // Wow such exception
            e.printStackTrace();
        }
    }


    private void saveSearchMovieListToDb(final List<SearchMovie> list){
        // TEST
        final MovieMapper movieMapper = new MovieMapper();

        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    List<SearchMovieRealm> movies = movieMapper.toSearchRealmList(list);
                    RealmList<SearchMovieRealm> _movies = new RealmList<>();
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
                    error.printStackTrace();
                }
            });
        } catch (Exception e) {
            // Wow such exception
            e.printStackTrace();
        } finally {
            // Wow such finally
        }
    }


    /* Keyboard state*/
    private void registerKeyboardListener() {
        // get Unregistrar
        mUnregistrar = KeyboardVisibilityEvent.registerEventListener(
                (Activity) mContext,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        mListener.onKeyboardStateChanged(isOpen);
                    }
                });
    }


    protected boolean isConnectedToNetwork() {

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }



    public interface CheckKeyboardState {
        void onKeyboardStateChanged(boolean isOpen);
    }

}
