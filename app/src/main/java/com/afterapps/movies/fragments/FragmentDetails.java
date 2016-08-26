package com.afterapps.movies.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afterapps.movies.R;
import com.afterapps.movies.Utilities;
import com.afterapps.movies.activities.ActivityMain;
import com.afterapps.movies.adapters.CastMembersRecyclerAdapter;
import com.afterapps.movies.adapters.ReviewsRecyclerAdapter;
import com.afterapps.movies.adapters.VideosRecyclerAdapter;
import com.afterapps.movies.datamodel.Cast;
import com.afterapps.movies.datamodel.RealmObjectCastMember;
import com.afterapps.movies.datamodel.RealmObjectMovie;
import com.afterapps.movies.datamodel.RealmObjectReview;
import com.afterapps.movies.datamodel.RealmObjectVideo;
import com.afterapps.movies.datamodel.Reviews;
import com.afterapps.movies.datamodel.Videos;
import com.afterapps.movies.helpers.RetrofitClients;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentDetails extends Fragment {
    public static final String PERSON_ID_KEY = "personIdKey";
    String apiKey;
    Call<Reviews> reviewsCall;
    Call<Videos> videoCall;
    Call<Cast> castCall;
    Realm realm;
    RealmObjectMovie movie;
    FloatingActionButton favoriteFab;
    FloatingActionButton shareFab;
    String favoriteMessage;
    RetrofitClients.MovieDetailsClient moviesClient;
    ImageView posterImageView;
    ImageView backdropImageView;
    TextView titleTextView;
    TextView captionTextView;
    TextView overviewTextView;
    RecyclerView videosRecyclerView;
    RecyclerView reviewsRecyclerView;
    RecyclerView castRecyclerView;
    NestedScrollView parentScrollView;
    CoordinatorLayout parentCoordinator;
    int id;
    boolean isTwoPane;

    public FragmentDetails() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //closing Realm instance
        realm.close();

        if (videoCall != null) {
            videoCall.cancel();
            videoCall = null;
        }
        if (reviewsCall != null) {
            reviewsCall.cancel();
            reviewsCall = null;
        }
        if (castCall != null) {
            castCall.cancel();
            castCall = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        realm = Realm.getDefaultInstance();

        //getting api key from resources
        apiKey = getString(R.string.api_key);

        isTwoPane = getResources().getBoolean(R.bool.isTabletLand);

        //getting movie from intent if NOT two pane
        if (isTwoPane && savedInstanceState == null) {
            id = 0;
        } else {
            id = getActivity().getIntent().getIntExtra(ActivityMain.MOVIE_ID_KEY, 0);
        }

        initViews(rootView);

        initMovie();
        return rootView;
    }

    private void initMovie() {

        boolean loadingFailure = false;

        movie = realm.where(RealmObjectMovie.class)
                .equalTo("id", id).findFirst();
        if (movie == null) {
            if (!isTwoPane) {
                //showing error message and finishing host activity in case the ID is invalid.
                //this is just a redundancy in case the API sends an invalid ID to prevent the app from..
                //..attempting to load invalid data and crashing or misbehaving
                Toast.makeText(getActivity(), getString(R.string.error_getting_id), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
            loadingFailure = true;
        }

        if (loadingFailure) {
            //waiting for user input in case of loading failure in two pane mode,
            //failure in two pane mode means the user is yet to select a movie from the main view. thus, ID = 0
            hideViews();
        } else {

            DisplayMovieDetails();

            displayFavoriteIcon();

            setupFavoriteMessage();

            setupFavoriteFabListener();

            setupShareFabListener();

            getVideosAndReviewsAndCast();

        }
    }

    private void hideViews() {
        parentScrollView.setVisibility(View.INVISIBLE);
    }

    private void showViews() {
        parentScrollView.setVisibility(View.VISIBLE);
        parentScrollView.fullScroll(View.FOCUS_UP);
    }

    private void initViews(View rootView) {
        //initializing views
        posterImageView = (ImageView) rootView.findViewById(R.id.details_poster_image_view);
        backdropImageView = (ImageView) rootView.findViewById(R.id.details_backdrop_image_view);
        titleTextView = (TextView) rootView.findViewById(R.id.details_title_text_view);
        captionTextView = (TextView) rootView.findViewById(R.id.details_caption_text_view);
        overviewTextView = (TextView) rootView.findViewById(R.id.details_overview_text_view);
        videosRecyclerView = (RecyclerView) rootView.findViewById(R.id.videos_recycler);
        reviewsRecyclerView = (RecyclerView) rootView.findViewById(R.id.reviews_recycler);
        castRecyclerView = (RecyclerView) rootView.findViewById(R.id.cast_recycler);
        favoriteFab = (FloatingActionButton) rootView.findViewById(R.id.details_fab);
        shareFab = (FloatingActionButton) rootView.findViewById(R.id.share_fab);
        parentScrollView = (NestedScrollView) rootView.findViewById(R.id.details_parent);
        parentCoordinator = (CoordinatorLayout) rootView.findViewById(R.id.parent_coordinator_details);
    }


    private void DisplayMovieDetails() {

        //displaying movie details
        titleTextView.setText(movie.getTitle());

        overviewTextView.setText(movie.getOverview());

        String caption = String.format(getString(R.string.details_caption_format)
                , movie.getReleaseDate()
                , String.valueOf(movie.getVoteAverage()));

        captionTextView.setText(caption);

        String backdropURL = String.format(getString(R.string.image_url_format_original)
                , movie.getBackdropPath());

        String posterURL = String.format(getString(R.string.image_url_format_w300)
                , movie.getPosterPath());

        Picasso.with(getActivity())
                .load(posterURL)
                .placeholder(R.drawable.poster_placeholder_small)
                .fit()
                .centerCrop()
                .into(posterImageView);

        Picasso.with(getActivity())
                .load(backdropURL)
                .placeholder(R.drawable.backdrop_placeholder)
                .fit()
                .centerCrop()
                .into(backdropImageView);

        VideosRecyclerAdapter videosRecyclerAdapter = new VideosRecyclerAdapter(getActivity()
                , realm.where(RealmObjectVideo.class).equalTo("movieID", movie.getId()).findAll()
                , this);

        videosRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        videosRecyclerView.setAdapter(videosRecyclerAdapter);

        ReviewsRecyclerAdapter reviewsRecyclerAdapter = new ReviewsRecyclerAdapter(getActivity()
                , realm.where(RealmObjectReview.class).equalTo("movieID", movie.getId()).findAll()
                , this);

        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewsRecyclerView.setAdapter(reviewsRecyclerAdapter);

        CastMembersRecyclerAdapter castMembersRecyclerAdapter = new CastMembersRecyclerAdapter(getActivity()
                , realm.where(RealmObjectCastMember.class).equalTo("movieID", movie.getId()).findAllSorted("order")
                , this);

        castRecyclerView.setAdapter(castMembersRecyclerAdapter);
        castRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }


    private void getVideosAndReviewsAndCast() {
        //initializing Retrofit
        final String BASE_URL = "http://api.themoviedb.org/3/";
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        moviesClient = retrofit.create(RetrofitClients.MovieDetailsClient.class);


        //getting data
        reviewsCall = moviesClient.getMovieReviews(movie.getId(), apiKey);
        videoCall = moviesClient.getMovieVideos(movie.getId(), apiKey);
        castCall = moviesClient.getMovieCast(movie.getId(), apiKey);

        String DATA_LOG_TAG = "RetrofitDetailsLog";
        reviewsCall.enqueue(Utilities.getMovieReviewsCallBack(DATA_LOG_TAG, realm, movie));
        videoCall.enqueue(Utilities.getMovieVideosCallBack(DATA_LOG_TAG, realm, movie));
        castCall.enqueue(Utilities.getMovieCastCallBack(DATA_LOG_TAG, realm, movie));
    }


    private void setupFavoriteFabListener() {
        final boolean favoriteOld = movie.isFavorite();
        final String messageFinal = favoriteMessage;

        favoriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(parentCoordinator, messageFinal, Snackbar.LENGTH_SHORT).show();
                toggleMovieFavorite(favoriteOld);
                displayFavoriteIcon();
                setupFavoriteMessage();
                setupFavoriteFabListener();
                if (isTwoPane) {
                    //notifying MainActivity that changes are being made to favorites to trigger..
                    //..change view to Popular in case the change was removing the last favorite entry

                    //the explicit check here to make sure the fragment main is displayed is necessary in case..
                    //..user selects a movie on a tablet but in landscape mode, THEN rotates the device, making..
                    //..isTwoPane return a true while the app is NOT in two pane mode
                    if (getActivity().getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_main_tag)) != null)
                        ((ActivityMain) getActivity()).favoritesChanged();
                }
            }
        });
    }

    private void setupShareFabListener() {
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                //adding data to the share intent
                share.putExtra(Intent.EXTRA_TEXT,
                        String.format(getString(R.string.share_movie_format)
                                , movie.getTitle()
                                , movie.getId()));

                startActivity(Intent.createChooser(share
                        , String.format(getString(R.string.share_video_message_format)
                                , movie.getTitle())));

            }
        });
    }

    private void displayFavoriteIcon() {
        if (movie.isFavorite()) {
            favoriteFab.setImageResource(R.drawable.ic_favorite_true);
        } else {
            favoriteFab.setImageResource(R.drawable.ic_favorite_false);
        }
    }

    private void toggleMovieFavorite(boolean favoriteOld) {
        realm.beginTransaction();
        movie.setFavorite(!favoriteOld);
        realm.copyToRealmOrUpdate(movie);
        realm.commitTransaction();
    }

    private void setupFavoriteMessage() {
        if (movie.isFavorite()) {
            favoriteMessage = getString(R.string.favorite_message_removed);
        } else {
            favoriteMessage = getString(R.string.favorite_message_added);
        }
    }

    public void handleVideoKey(String key) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(String.format(getString(R.string.youtube_url_format), key))));
    }

    public void handleReviewUri(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(url)));
    }

    public void twoPaneDisplay(Integer id) {
        this.id = id;
        showViews();
        initMovie();
    }

    public void handleVideoShare(RealmObjectVideo video) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //adding data to the share intent
        share.putExtra(Intent.EXTRA_TEXT,
                String.format(getString(R.string.share_video_format)
                        , video.getMovieTitle()
                        , video.getName()
                        , video.getKey()));

        startActivity(Intent.createChooser(share, String.format(getString(R.string.share_video_message_format), video.getType())));
    }

    public void handleCastRecyclerClick(Integer id) {
        //startActivity(new Intent(Intent.ACTION_VIEW,
        //        Uri.parse(String.format(getString(R.string.cast_member_url_format), String.valueOf(id)))));

        //replaced opening IMDB link in browser by showing person details in a bottom sheet

        FragmentPersonDetails fragmentPersonDetails = new FragmentPersonDetails();
        Bundle arguments = new Bundle();
        arguments.putInt(PERSON_ID_KEY, id);
        fragmentPersonDetails.setArguments(arguments);
        fragmentPersonDetails.show(getActivity().getSupportFragmentManager(), fragmentPersonDetails.getTag());
    }
}
