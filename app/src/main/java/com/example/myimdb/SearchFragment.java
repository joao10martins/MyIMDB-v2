package com.example.myimdb;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.myimdb.adapters.SearchRecyclerAdapter;
import com.example.myimdb.helpers.GsonRequest;
import com.example.myimdb.helpers.SharedPreferencesHelper;
import com.example.myimdb.map.GenreMapper;
import com.example.myimdb.map.MovieMapper;
import com.example.myimdb.model.response.MovieDetails;
import com.example.myimdb.model.response.MovieGenre;
import com.example.myimdb.model.realm.MovieGenreRealm;
import com.example.myimdb.model.response.MovieGenreResults;
import com.example.myimdb.model.response.SearchMovie;
import com.example.myimdb.model.realm.SearchMovieRealm;
import com.example.myimdb.model.response.SearchMovieResults;


import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements SearchRecyclerAdapter.OnMovieClick{

    private View mView;
    private Context mContext;
    private Unregistrar mUnregistrar;
    private InputMethodManager mInputManager;


    private RequestQueue mRequestQueue;
    private String mUrl;
    private RecyclerView mRecyclerView;
    private EditText query_text;

    //private List<MovieGenre> mGenreList = new ArrayList<>();
    /* Variables */

    private HashMap<Integer , MovieGenreRealm> mGenreMap = new HashMap<>();
    private List<MovieGenre> genreList = new ArrayList<>();
    private SearchRecyclerAdapter mAdapter;
    private int mListCount;
    private String mSearch_query;
    private int mTotalPages;
    RealmResults<SearchMovieRealm> mResults;
    private boolean isMovieViewAsList = false;
    private boolean isFavoritesFromSearch = false;
    //private int testCount;
    private boolean isFilterByName = false;
    private boolean isFilterByReleaseDate = false;
    private boolean isFilterByRating = false;
    private CheckBox nameCheckbox;
    private CheckBox dateCheckbox;
    private CheckBox ratingCheckbox;

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

        //mToolbarOptions = (ToolbarOptions) context;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_search, container, false);

        SharedPreferencesHelper prefs = SharedPreferencesHelper.getInstance();
        isMovieViewAsList = Boolean.valueOf(prefs.getPreferences("search_viewMode", "false"));
        mSearch_query = prefs.getPreferences("search_query", null);

        /* Setting the Filter Mode if there is one */
        isFilterByName = Boolean.valueOf(prefs.getPreferences("search_filterByName", "false"));
        isFilterByReleaseDate = Boolean.valueOf(prefs.getPreferences("search_filterByReleaseDate", "false"));
        isFilterByRating = Boolean.valueOf(prefs.getPreferences("search_filterByRating", "false"));





        // get db
        mRealm = Realm.getDefaultInstance();



        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mContext = getActivity();
        mRecyclerView = mView.findViewById(R.id.rvSearch);
        query_text = mView.findViewById(R.id.search_movie);
        mInputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);


        /*mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<MovieGenreRealm> rows = realm.where(MovieGenreRealm.class).findAll();
                rows.deleteAllFromRealm();
            }
        });*/

        /*mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<SearchMovieRealm> rows = realm.where(SearchMovieRealm.class).findAll();
                rows.deleteAllFromRealm();
            }
        });*/

        registerKeyboardListener();

        // Initialize the list of Genres.
        getGenreList();
        /* Setting the Filter Mode if there is one */
        if (isFilterByName && mAdapter != null){
            RealmResults<SearchMovieRealm> filteredByName = mAdapter.getData().sort("title", Sort.ASCENDING);
            mAdapter.updateData(filteredByName);
            mRecyclerView.setAdapter(mAdapter);
            rvItemAnim();
        }
        if (isFilterByReleaseDate && mAdapter != null){
            RealmResults<SearchMovieRealm> filteredByReleaseDate = mAdapter.getData().sort("release_date", Sort.DESCENDING);
            mAdapter.updateData(filteredByReleaseDate);
            mRecyclerView.setAdapter(mAdapter);
            rvItemAnim();
        }
        if (isFilterByRating && mAdapter != null) {
            RealmResults<SearchMovieRealm> filteredByRating = mAdapter.getData().sort("vote_average", Sort.DESCENDING);
            mAdapter.updateData(filteredByRating);
            mRecyclerView.setAdapter(mAdapter);
            rvItemAnim();
        }
        mRecyclerView.setLayoutManager(isMovieViewAsList ? new GridLayoutManager(getContext(), 2) : new LinearLayoutManager(getContext()));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        ActionBar toolbar = ((MainActivity)getActivity()).getSupportActionBar();
        toolbar.setTitle("Search");
        menu.findItem(R.id.toolbar_favorites).setVisible(true);
        menu.findItem(R.id.toolbar_visualization).setVisible(true);
        menu.findItem(R.id.toolbar_sort).setVisible(true);
        //menu.findItem(R.id.toolbar_profile).setVisible(true);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.toolbar_favorites:
                //switchFragment();
                // Get the FragmentManager and start a transaction.
                FavoritesFragment favoritesFragment = FavoritesFragment.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                // Replace the fragment
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.add(R.id.fragment_container,
                        favoritesFragment);
                ActionBar toolbar = ((MainActivity)getActivity()).getSupportActionBar();
                toolbar.setTitle("Favorites");
                fragmentTransaction.commit();

                isFavoritesFromSearch = true;

                mListener.onFavoritesClick(isFavoritesFromSearch);

                return true;
            case R.id.toolbar_visualization:

                // change between List and Grid layout(default: Linear)
                if (mSearch_query != null){
                    isMovieViewAsList = !isMovieViewAsList;
                    int scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();


                    /*RealmResults<SearchMovieRealm> results = mRealm.where(SearchMovieRealm.class)
                            .like("title", "*"+mSearch_query.trim()+"*", Case.INSENSITIVE) // Finds any value that have 'mSearchQuery' in any position.
                            .findAllAsync();*/
                    // TODO: if there is a filter applied, change adapter
                    mAdapter = new SearchRecyclerAdapter(getContext(), mAdapter.getData(),SearchFragment.this, isMovieViewAsList);
                    mRecyclerView.setAdapter(mAdapter);
                    rvItemAnim();


                    if(mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                        mRecyclerView.setLayoutManager(isMovieViewAsList ? new GridLayoutManager(getContext(), 2) : new LinearLayoutManager(getContext()));
                    } else {
                        mRecyclerView.setLayoutManager(isMovieViewAsList ? new GridLayoutManager(getContext(), 2) : new LinearLayoutManager(getContext()));
                    }
                    mRecyclerView.scrollToPosition(scrollPosition);
                }
                return true;

            case R.id.toolbar_sort:
                /* custom popup */
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
                            RealmResults<SearchMovieRealm> filteredByName = mAdapter.getData().sort("title", Sort.ASCENDING);
                            mAdapter.updateData(filteredByName);
                            mRecyclerView.setAdapter(mAdapter);
                            rvItemAnim();
                            alertDialog.dismiss();
                        } else {
                            // remove filter
                            RealmResults<SearchMovieRealm> unfilteredResults = mRealm.where(SearchMovieRealm.class)
                                    .like("title", "*"+mSearch_query.trim()+"*", Case.INSENSITIVE) // Finds any value that have 'mSearchQuery' in any position.
                                    .findAllAsync();
                            mAdapter.updateData(unfilteredResults);
                            mRecyclerView.setAdapter(mAdapter);
                            rvItemAnim();
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
                            RealmResults<SearchMovieRealm> filteredByReleaseDate = mAdapter.getData().sort("release_date", Sort.DESCENDING);
                            mAdapter.updateData(filteredByReleaseDate);
                            mRecyclerView.setAdapter(mAdapter);
                            rvItemAnim();
                            alertDialog.dismiss();
                        } else {
                            // remove filter
                            RealmResults<SearchMovieRealm> unfilteredResults = mRealm.where(SearchMovieRealm.class)
                                    .like("title", "*"+mSearch_query.trim()+"*", Case.INSENSITIVE) // Finds any value that have 'mSearchQuery' in any position.
                                    .findAllAsync();
                            mAdapter.updateData(unfilteredResults);
                            mRecyclerView.setAdapter(mAdapter);
                            rvItemAnim();
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
                            RealmResults<SearchMovieRealm> filteredByRating = mAdapter.getData().sort("vote_average", Sort.DESCENDING);
                            mAdapter.updateData(filteredByRating);
                            mRecyclerView.setAdapter(mAdapter);
                            rvItemAnim();
                            alertDialog.dismiss();
                        } else {
                            // remove filter
                            RealmResults<SearchMovieRealm> unfilteredResults = mRealm.where(SearchMovieRealm.class)
                                    .like("title", "*"+mSearch_query.trim()+"*", Case.INSENSITIVE) // Finds any value that have 'mSearchQuery' in any position.
                                    .findAllAsync();
                            mAdapter.updateData(unfilteredResults);
                            mRecyclerView.setAdapter(mAdapter);
                            rvItemAnim();
                            alertDialog.dismiss();
                        }
                    }
                });
                return true;

        }

        return super.onOptionsItemSelected(item);
        //return false;
    }



    @Override
    public void onDestroy() {
        mUnregistrar.unregister();
        super.onDestroy();
    }



    // Get List of all the existing Genres in the API.
    private void getGenreList() {
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        try {
            // If there is no Genres data on Realm, try to fetch it from the API, if internet connection is available.
            if (mGenreMap.isEmpty() && mRealm.where(MovieGenreRealm.class).findAll().isEmpty()) {
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
                try {
                    genreList.addAll(response.genreList);
                    saveGenresToDb(genreList);
                    if (!mRealm.where(MovieGenreRealm.class).findAll().containsAll(genreList)){
                        for (MovieGenreRealm movieGenre : mRealm.where(MovieGenreRealm.class).findAllAsync()) {
                            mGenreMap.put(movieGenre.getId(), movieGenre);
                        }
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
        if (mSearch_query != null){
            // if there already was a search
            query_text.setText(mSearch_query.trim());
            search();
            // also enable next search
            enableSearch();
        } else {
            enableSearch();
        }



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

                            mAdapter = new SearchRecyclerAdapter(getContext(), mResults, SearchFragment.this, isMovieViewAsList);
                            mRecyclerView.setAdapter(mAdapter);
                            rvItemAnim();
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
    public void onItemClick(int movieId, String title) {
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


        mListener.onDetailClick(title);
        mListener.isFromSearch(true);


    }


    private Response.Listener<MovieDetails> getDetailsSuccessListener() {
        return new Response.Listener<MovieDetails>() {
            @Override
            public void onResponse(MovieDetails response) {

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
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                toolbar.getMenu().findItem(R.id.toolbar_visualization).setVisible(false);
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


    public void search(){

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

            mResults = mRealm.where(SearchMovieRealm.class)
                    .like("title", "*"+mSearch_query.trim()+"*", Case.INSENSITIVE) // Finds any value that have 'mSearchQuery' in any position.
                    .findAllAsync();
            mInputManager.hideSoftInputFromWindow((null == mView) ? null : mView.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            //mRecyclerView.setLayoutManager(isMovieViewAsList ? new GridLayoutManager(getContext(), 2) : new LinearLayoutManager(getContext()));
            if (isFilterByName && mAdapter != null){
                RealmResults<SearchMovieRealm> filteredByName = mAdapter.getData().sort("title", Sort.ASCENDING);
                mAdapter.updateData(filteredByName);
                mRecyclerView.setAdapter(mAdapter);
                rvItemAnim();
            }
            if (isFilterByReleaseDate && mAdapter != null){
                RealmResults<SearchMovieRealm> filteredByReleaseDate = mAdapter.getData().sort("release_date", Sort.DESCENDING);
                mAdapter.updateData(filteredByReleaseDate);
                mRecyclerView.setAdapter(mAdapter);
                rvItemAnim();
            }
            if (isFilterByRating && mAdapter != null) {
                RealmResults<SearchMovieRealm> filteredByRating = mAdapter.getData().sort("vote_average", Sort.DESCENDING);
                mAdapter.updateData(filteredByRating);
                mRecyclerView.setAdapter(mAdapter);
                rvItemAnim();
            }

            if (mResults != null && mRecyclerView != null){
                mAdapter = new SearchRecyclerAdapter(getContext(), mResults, SearchFragment.this, isMovieViewAsList);
                mRecyclerView.setAdapter(mAdapter);
                rvItemAnim();
                mRecyclerView.setLayoutManager(isMovieViewAsList ? new GridLayoutManager(getContext(), 2) : new LinearLayoutManager(getContext()));
            } else {
                mAdapter.notifyDataSetChanged();
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
                        mAdapter = new SearchRecyclerAdapter(getContext(), mResults, SearchFragment.this, isMovieViewAsList);
                        mRecyclerView.setAdapter(mAdapter);
                        rvItemAnim();
                        mRecyclerView.setLayoutManager(isMovieViewAsList ? new GridLayoutManager(getContext(), 2) : new LinearLayoutManager(getContext()));
                    } else {
                        //Show disconnected message
                        Toast.makeText(getContext(), "Network connection unavailable", Toast.LENGTH_SHORT).show();
                    }

                }
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

    private void enableSearch(){
        query_text.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        query_text.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // If triggered by an enter key, this is the event; otherwise, this is null.
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    /* Setting the Filter Mode if there is one */
                    if (isFilterByName && mAdapter != null){
                        RealmResults<SearchMovieRealm> filteredByName = mAdapter.getData().sort("title", Sort.ASCENDING);
                        mAdapter.updateData(filteredByName);
                        mRecyclerView.setAdapter(mAdapter);
                        rvItemAnim();
                    }
                    if (isFilterByReleaseDate && mAdapter != null){
                        RealmResults<SearchMovieRealm> filteredByReleaseDate = mAdapter.getData().sort("release_date", Sort.DESCENDING);
                        mAdapter.updateData(filteredByReleaseDate);
                        mRecyclerView.setAdapter(mAdapter);
                        rvItemAnim();
                    }
                    if (isFilterByRating && mAdapter != null) {
                        RealmResults<SearchMovieRealm> filteredByRating = mAdapter.getData().sort("vote_average", Sort.DESCENDING);
                        mAdapter.updateData(filteredByRating);
                        mRecyclerView.setAdapter(mAdapter);
                        rvItemAnim();
                    }
                    mRecyclerView.setLayoutManager(isMovieViewAsList ? new GridLayoutManager(getContext(), 2) : new LinearLayoutManager(getContext()));
                    return true;
                }
                return false;
            }
        });


        ImageButton btnSearch = mView.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mRecyclerView.setLayoutManager(isMovieViewAsList ? new GridLayoutManager(getContext(), 2) : new LinearLayoutManager(getContext()));
                search();
                /* Setting the Filter Mode if there is one */
                if (isFilterByName && mAdapter != null){
                    RealmResults<SearchMovieRealm> filteredByName = mAdapter.getData().sort("title", Sort.ASCENDING);
                    mAdapter.updateData(filteredByName);
                    mRecyclerView.setAdapter(mAdapter);
                    rvItemAnim();
                }
                if (isFilterByReleaseDate && mAdapter != null){
                    RealmResults<SearchMovieRealm> filteredByReleaseDate = mAdapter.getData().sort("release_date", Sort.DESCENDING);
                    mAdapter.updateData(filteredByReleaseDate);
                    mRecyclerView.setAdapter(mAdapter);
                    rvItemAnim();
                }
                if (isFilterByRating && mAdapter != null) {
                    RealmResults<SearchMovieRealm> filteredByRating = mAdapter.getData().sort("vote_average", Sort.DESCENDING);
                    mAdapter.updateData(filteredByRating);
                    mRecyclerView.setAdapter(mAdapter);
                    rvItemAnim();
                }
                mRecyclerView.setLayoutManager(isMovieViewAsList ? new GridLayoutManager(getContext(), 2) : new LinearLayoutManager(getContext()));

                // TODO: display correct layout on recyclerview accordingly with last viewMode

            }
        });
    }



    /* RecyclerView Populate Animation */
    private void rvItemAnim() {

        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                for (int i=0; i < mRecyclerView.getChildCount(); i++) {
                    View v = mRecyclerView.getChildAt(i);
                    v.setAlpha(0.0f);
                    v.animate().alpha(1.0f)
                            .setDuration(900)
                            .setStartDelay(i * 50)
                            .start();
                }
                return true;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Search");
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        if (mSearch_query != null){
            SharedPreferencesHelper.getInstance().setPreferences("search_viewMode", String.valueOf(isMovieViewAsList));
            SharedPreferencesHelper.getInstance().setPreferences("search_query", mSearch_query.trim());

            /* Save filters */
            SharedPreferencesHelper.getInstance().setPreferences("search_filterByName", String.valueOf(isFilterByName));
            SharedPreferencesHelper.getInstance().setPreferences("search_filterByReleaseDate", String.valueOf(isFilterByReleaseDate));
            SharedPreferencesHelper.getInstance().setPreferences("search_filterByRating", String.valueOf(isFilterByRating));
        }

    }




    public interface CheckKeyboardState {
        void onKeyboardStateChanged(boolean isOpen);
        void onDetailClick(String title);
        void isFromSearch(boolean isFromSearch);
        void onFavoritesClick(boolean favoritesFromSearch);
        int getBackCount();
    }





}
