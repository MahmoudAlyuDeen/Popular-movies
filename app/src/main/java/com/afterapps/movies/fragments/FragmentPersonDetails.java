package com.afterapps.movies.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.afterapps.movies.R;
import com.afterapps.movies.Utilities;
import com.afterapps.movies.adapters.MoviesKnownForRecyclerAdapter;
import com.afterapps.movies.datamodel.KnownFor;
import com.afterapps.movies.datamodel.MoviesByPerson;
import com.afterapps.movies.datamodel.Person;
import com.afterapps.movies.helpers.RetrofitClients;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Credits:
//http://code.tutsplus.com/articles/how-to-use-bottom-sheets-with-the-design-support-library--cms-26031
public class FragmentPersonDetails extends BottomSheetDialogFragment {

    RetrofitClients.PersonDetailsClient personDetailsClient;
    NestedScrollView personDetailsScrollView;
    View personDetailsContentParent;
    View personDetailsKnownForParent;
    ImageView personDetailsImageView;
    TextView personDetailsBirthdayTextView;
    TextView personDetailsBirthPlaceTextView;
    TextView personDetailsBiographyTextView;
    TextView personDetailsNameTextView;
    RecyclerView personDetailsKnownForRecyclerView;
    ProgressBar personDetailsProgressBar;
    View imdbLogo;
    Call<Person> personDetailsCall;
    Call<MoviesByPerson> personKnownForCall;
    String apiKey;


    public FragmentPersonDetails() {
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View rootView = View.inflate(getContext(), R.layout.fragment_person_details, null);
        initializeViews(rootView);
        dialog.setContentView(rootView);

        //getting api key from resources
        apiKey = getString(R.string.api_key);

        //getting person ID
        final int personID = getArguments().getInt(FragmentDetails.PERSON_ID_KEY);

        //initially hiding views till loading is finished
        hideView();

        //initializing details loading
        getPersonDetails(personID);

        //getting BottomSheetBehavior from dialog's parent view
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) rootView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        //calculating person profile picture height and setting bottom sheet peek height..
        //..to a lower value so that only most the picture is displayed indicating to the users they can scroll up
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.person_details_profile_placeholder);
            int peekHeight = 0;
            peekHeight += drawable.getIntrinsicHeight();
            peekHeight -= Utilities.convertDpToPixel(16, getActivity());
            ((BottomSheetBehavior) behavior).setPeekHeight(peekHeight);
            setupProgressBarMargin(peekHeight);
        }
    }

    private void setupProgressBarMargin(int peekHeight) {
        //centering progress bar within peek height
        float progressBarSize = getResources().getDimension(R.dimen.progress_bar_size);
        personDetailsProgressBar.setY(peekHeight / 2 - progressBarSize / 2);
    }

    private void hideView() {
        //hiding dialog parent and movies recycler till each loading is finished
        personDetailsContentParent.setVisibility(View.INVISIBLE);
        personDetailsKnownForParent.setVisibility(View.INVISIBLE);
        personDetailsProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //cancelling any network calls still in progress
        if (personDetailsCall != null) {
            personDetailsCall.cancel();
            personDetailsCall = null;
        }
        if (personKnownForCall != null) {
            personKnownForCall.cancel();
            personKnownForCall = null;
        }
    }

    private void initializeViews(View rootView) {
        //initializing views
        personDetailsScrollView = (NestedScrollView) rootView.findViewById(R.id.person_details_bottom_sheet);
        personDetailsContentParent = rootView.findViewById(R.id.person_details_bottom_sheet_content_parent);
        personDetailsKnownForParent = rootView.findViewById(R.id.person_details_known_for_parent);
        personDetailsImageView = (ImageView) rootView.findViewById(R.id.person_details_profile_image_view);
        personDetailsBiographyTextView = (TextView) rootView.findViewById(R.id.person_biography_text_view);
        personDetailsBirthdayTextView = (TextView) rootView.findViewById(R.id.person_birth_date_text_view);
        personDetailsBirthPlaceTextView = (TextView) rootView.findViewById(R.id.person_birth_place_text_view);
        personDetailsNameTextView = (TextView) rootView.findViewById(R.id.person_details_name_text_view);
        personDetailsKnownForRecyclerView = (RecyclerView) rootView.findViewById(R.id.person_details_known_for_recycler);
        personDetailsProgressBar = (ProgressBar) rootView.findViewById(R.id.person_details_progress_bar);
        imdbLogo = rootView.findViewById(R.id.person_details_imdb_logo);
    }

    private void getPersonDetails(Integer id) {
        //initializing Retrofit and GSON
        final String BASE_URL = "http://api.themoviedb.org/3/";
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        personDetailsClient = retrofit.create(RetrofitClients.PersonDetailsClient.class);

        //getting data
        personDetailsCall = personDetailsClient.getPersonDetails(id, apiKey);
        personKnownForCall = personDetailsClient.getKnownFor(id, apiKey);

        String DATA_LOG_TAG = "RetrofitPersonDetailsLog";
        personDetailsCall.enqueue(getPersonDetailsCallBack(DATA_LOG_TAG));
        personKnownForCall.enqueue(getPersonKnownForCallBack(DATA_LOG_TAG));
    }

    private Callback<MoviesByPerson> getPersonKnownForCallBack(final String DATA_LOG_TAG) {
        return new Callback<MoviesByPerson>() {
            @Override
            public void onResponse(Call<MoviesByPerson> call, Response<MoviesByPerson> response) {
                Log.d(DATA_LOG_TAG, "movies by person response");
                if (response.isSuccessful()) {
                    Log.d(DATA_LOG_TAG, "movies by person success");
                    final List<KnownFor> movies = response.body().getCastOf();
                    populateKnownForRecycler(movies);
                } else
                    Log.d(DATA_LOG_TAG, response.message());
            }

            @Override
            public void onFailure(Call<MoviesByPerson> call, Throwable t) {
                //if (retrofitCurrentRetryCount < RETROFIT_RETRY_COUNT) {
                //    call.clone().enqueue(this);
                //    retrofitCurrentRetryCount++;
                //}
                Log.d(DATA_LOG_TAG, "movies by person failed");
            }
        };
    }

    private Callback<Person> getPersonDetailsCallBack(final String DATA_LOG_TAG) {
        return new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                Log.d(DATA_LOG_TAG, "person details response");
                if (response.isSuccessful()) {
                    Log.d(DATA_LOG_TAG, "person details success");
                    final Person person = response.body();
                    setupImdbLink(person.getImdbId());
                    displayPersonDetails(person);
                } else
                    Log.d(DATA_LOG_TAG, response.message());
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {
                //if (retrofitCurrentRetryCount < RETROFIT_RETRY_COUNT) {
                //    call.clone().enqueue(this);
                //    retrofitCurrentRetryCount++;
                //}
                Log.d(DATA_LOG_TAG, "person details failed");
                if (isAdded())
                    dismissBottomSheet();
            }
        };
    }

    private void dismissBottomSheet() {
        //dismissing bottom sheet on loading failure if still showing
        dismiss();
        Toast.makeText(getActivity(),
                getString(R.string.person_details_network_failure),
                Toast.LENGTH_SHORT).show();
    }

    private void setupImdbLink(final String imdbId) {
        imdbLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(String.format(getString(R.string.imdb_person_url_format), imdbId))));
            }
        });
    }

    private void displayPersonDetails(Person person) {
        String posterURL = String.format(getString(R.string.image_url_format_w500)
                , person.getProfilePath());

        Picasso.with(getActivity())
                .load(posterURL)
                .placeholder(R.drawable.person_details_profile_placeholder)
                .fit()
                .centerCrop()
                .into(personDetailsImageView);

        //hiding progress bar and showing displaying person details
        personDetailsProgressBar.setVisibility(View.INVISIBLE);
        personDetailsContentParent.setVisibility(View.VISIBLE);
        personDetailsBirthPlaceTextView.setText(person.getPlaceOfBirth() == null || person.getPlaceOfBirth().isEmpty() ? getString(R.string.not_available) : person.getPlaceOfBirth());
        personDetailsBirthdayTextView.setText(person.getBirthday() == null || person.getBirthday().isEmpty() ? getString(R.string.not_available) : person.getBirthday());
        personDetailsBiographyTextView.setText(person.getBiography() == null || person.getBiography().isEmpty() ? getString(R.string.not_available) : person.getBiography());
        personDetailsNameTextView.setText(person.getName() == null || person.getName().isEmpty() ? getString(R.string.not_available) : person.getName());
        personDetailsScrollView.fullScroll(View.FOCUS_UP);
    }

    private void populateKnownForRecycler(List<KnownFor> movies) {
        //showing known for recycler and populating it with movies
        personDetailsKnownForParent.setVisibility(View.VISIBLE);

        MoviesKnownForRecyclerAdapter moviesKnownForRecyclerAdapter = new MoviesKnownForRecyclerAdapter(getActivity(), movies);

        personDetailsKnownForRecyclerView.setAdapter(moviesKnownForRecyclerAdapter);
        personDetailsKnownForRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }
}
