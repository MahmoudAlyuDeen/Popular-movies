package com.afterapps.movies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.afterapps.movies.R;
import com.afterapps.movies.fragments.FragmentDetails;
import com.afterapps.movies.fragments.FragmentMain;

public class ActivityMain extends AppCompatActivity {

    public static final String MOVIE_ID_KEY = "movieIDKey";
    FragmentDetails fragmentDetails;
    FragmentMain fragmentMain;
    boolean isTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isTwoPane = getResources().getBoolean(R.bool.isTabletLand);
        if (isTwoPane) {
            fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_main_tag));
            fragmentDetails = (FragmentDetails) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_details_tag));
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isTwoPane = getResources().getBoolean(R.bool.isTabletLand);
        if (isTwoPane) {
            fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_main_tag));
            fragmentDetails = (FragmentDetails) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_details_tag));
        }
    }

    public void handleRecyclerClick(Integer id) {
        if (isTwoPane) {
            fragmentDetails.twoPaneDisplay(id);
        } else {
            Intent intent = new Intent(this, ActivityDetails.class);
            intent.putExtra(MOVIE_ID_KEY, id);
            startActivity(intent);
        }
    }

    public void favoritesChanged() {
        if (isTwoPane) {
            fragmentMain.favoritesChanged();
        }
    }
}
