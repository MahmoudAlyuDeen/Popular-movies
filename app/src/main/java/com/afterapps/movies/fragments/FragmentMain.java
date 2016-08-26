package com.afterapps.movies.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afterapps.movies.R;
import com.afterapps.movies.Utilities;
import com.afterapps.movies.adapters.MoviesRecyclerAdapter;
import com.afterapps.movies.datamodel.Movies;
import com.afterapps.movies.datamodel.RealmObjectMovie;
import com.afterapps.movies.helpers.RetrofitClients;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentMain extends Fragment {
    private static final int MOVIE_LIST_TYPE_POPULAR = 0;
    private static final int MOVIE_LIST_TYPE_UPCOMING = 1;
    private static final int MOVIE_LIST_TYPE_TOP_RATED = 2;
    private static final int MOVIE_LIST_TYPE_FAVORITE = 3;
    SwipeRefreshLayout swipeRefreshLayout;
    RetrofitClients.AllMoviesClient moviesClient;
    Call<Movies> popularMoviesCall;
    Call<Movies> topRatedMoviesCall;
    Call<Movies> upcomingMoviesCall;
    Realm realm;
    RecyclerView moviesRecycler;
    MoviesRecyclerAdapter moviesRecyclerAdapter;
    int listType;
    String apiKey;

    public FragmentMain() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_activity_main, menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //closing Realm instance
        realm.close();

        //cancelling calls if in progress
        if (popularMoviesCall != null) {
            popularMoviesCall.cancel();
            popularMoviesCall = null;
        }
        if (topRatedMoviesCall != null) {
            topRatedMoviesCall.cancel();
            topRatedMoviesCall = null;
        }
        if (upcomingMoviesCall != null) {
            upcomingMoviesCall.cancel();
            upcomingMoviesCall = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sort) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
            final String[] sortTypes = getResources().getStringArray(R.array.sort_types_array);
            builder.setSingleChoiceItems(sortTypes, listType, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listType = which;
                    changeListType(which);
                    dialog.dismiss();
                }
            });
            builder.setCancelable(true);
            builder.setTitle(R.string.sort_dialog_title);
            builder.show();
        }
        return true;
    }

    private void changeListType(int which) {
        listType = which;

        //saving new type to preferences
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.perf_sort_type_key), listType);
        editor.apply();

        //refreshing list
        populateRecyclerWithCurrentData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        realm = Realm.getDefaultInstance();

        //getting api key from resources
        apiKey = getString(R.string.api_key);

        //getting sort type from shared preferences
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        listType = sharedPref.getInt(getString(R.string.perf_sort_type_key), MOVIE_LIST_TYPE_POPULAR);

        initializeViews(rootView);

        getMovies();

        populateRecyclerWithCurrentData();

        showLoadingIfNoData();

        setupSwipeRefresh();

        return rootView;
    }

    private void showLoadingIfNoData() {
        //because for some reason, .setRefreshing(true); won't do it..!
        if (moviesRecyclerAdapter.getData() == null)
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        else if (moviesRecyclerAdapter.getData().size() == 0)
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
    }

    private void initializeViews(View rootView) {
        moviesRecycler = (RecyclerView) rootView.findViewById(R.id.movies_recycler);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.main_swipe_refresh);
    }

    private void getMovies() {
        //initializing Retrofit
        final String BASE_URL = "http://api.themoviedb.org/3/";
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        moviesClient = retrofit.create(RetrofitClients.AllMoviesClient.class);

        //getting data
        popularMoviesCall = moviesClient.getPopularMovies(apiKey);
        upcomingMoviesCall = moviesClient.getUpcomingMovies(apiKey);
        topRatedMoviesCall = moviesClient.getTopRatedMovies(apiKey);

        final String DATA_LOG_TAG = "RetrofitMainLog";
        popularMoviesCall.enqueue(Utilities.getPopularMovies(realm, DATA_LOG_TAG,
                MOVIE_LIST_TYPE_POPULAR, swipeRefreshLayout));

        topRatedMoviesCall.enqueue(Utilities.getTopRatedMovies(realm, DATA_LOG_TAG,
                MOVIE_LIST_TYPE_TOP_RATED, swipeRefreshLayout));

        upcomingMoviesCall.enqueue(Utilities.getUpcomingMovies(realm, DATA_LOG_TAG,
                MOVIE_LIST_TYPE_UPCOMING, swipeRefreshLayout));
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMovies();
            }
        });
    }

    private void populateRecyclerWithCurrentData() {
        //initializing adapter with appropriate data
        if (listType == MOVIE_LIST_TYPE_FAVORITE) {
            RealmResults<RealmObjectMovie> favorites = realm.where(RealmObjectMovie.class)
                    .equalTo("favorite", true).findAll();
            if (favorites.size() == 0) {
                changeListType(MOVIE_LIST_TYPE_POPULAR);
                Toast.makeText(getActivity()
                        , getString(R.string.empty_favorite_toast), Toast.LENGTH_SHORT).show();
            } else {
                moviesRecyclerAdapter = new MoviesRecyclerAdapter(getActivity(), favorites);
            }
        } else {
            moviesRecyclerAdapter = new MoviesRecyclerAdapter(getActivity()
                    , realm.where(RealmObjectMovie.class)
                    .equalTo("listType", listType).findAll());
        }

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        //initializing layout manager with appropriate grid width
        //ugliest piece of code I've ever written!
        //#cringy
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (isTablet) {
                // tablet + portrait = 4
                moviesRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 4));
            } else {
                // phone + portrait = 2
                moviesRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            }
        } else {
            if (isTablet) {
                // tablet + landscape (2 pane) = 3
                moviesRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            } else {
                // phone + landscape = 4
                moviesRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 4));
            }
        }
        moviesRecycler.setAdapter(moviesRecyclerAdapter);
    }

    public void favoritesChanged() {
        if (listType == MOVIE_LIST_TYPE_FAVORITE) {
            //making sure never to show the user an empty favorite screen
            //in two pane mode
            populateRecyclerWithCurrentData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listType == MOVIE_LIST_TYPE_FAVORITE) {
            //making sure never to show the user an empty favorite screen
            //in single pane mode
            populateRecyclerWithCurrentData();
        }
    }
}
