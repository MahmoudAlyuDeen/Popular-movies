package com.afterapps.movies;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;

import com.afterapps.movies.datamodel.Cast;
import com.afterapps.movies.datamodel.CastMember;
import com.afterapps.movies.datamodel.Movie;
import com.afterapps.movies.datamodel.Movies;
import com.afterapps.movies.datamodel.RealmObjectCastMember;
import com.afterapps.movies.datamodel.RealmObjectMovie;
import com.afterapps.movies.datamodel.RealmObjectReview;
import com.afterapps.movies.datamodel.RealmObjectVideo;
import com.afterapps.movies.datamodel.Review;
import com.afterapps.movies.datamodel.Reviews;
import com.afterapps.movies.datamodel.Video;
import com.afterapps.movies.datamodel.Videos;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Created by Mahmoud AlyuDeen on 8/15/2016.
 */
public class Utilities {
    //generates a unique ID to be stored as PrimaryKey in realm
    public static long getNextCastMemberUniqID(Realm realm) {
        Number number = realm.where(RealmObjectCastMember.class).max("uniqID");
        if (number == null) return 1;
        else return (long) number + 1;
    }

    //generates a unique ID to be stored as PrimaryKey in realm
    public static long getNextMovieUniqID(Realm realm) {
        Number number = realm.where(RealmObjectMovie.class).max("uniqID");
        if (number == null) return 1;
        else return (long) number + 1;
    }

    public static Callback<Movies> getPopularMovies(final Realm realm
            , final String DATA_LOG_TAG
            , final int listType
            , final SwipeRefreshLayout swipeRefreshLayout) {
        return new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Log.d(DATA_LOG_TAG, "response");
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    Log.d(DATA_LOG_TAG, "success");
                    List<Movie> movies = response.body().getMovies();
                    Utilities.addMoviesToRealm(movies, listType, realm);
                } else
                    Log.d(DATA_LOG_TAG, response.message());
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                //if (retrofitCurrentRetryCount < RETROFIT_RETRY_COUNT) {
                //    call.clone().enqueue(this);
                //    retrofitCurrentRetryCount++;
                //}
                swipeRefreshLayout.setRefreshing(false);
                Log.d(DATA_LOG_TAG, "failed");
            }
        };
    }
    
    public static Callback<Movies> getUpcomingMovies(final Realm realm
            , final String DATA_LOG_TAG
            , final int listType
            , final SwipeRefreshLayout swipeRefreshLayout) {
        return new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Log.d(DATA_LOG_TAG, "response");
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    Log.d(DATA_LOG_TAG, "success");
                    List<Movie> movies = response.body().getMovies();
                    Utilities.addMoviesToRealm(movies, listType, realm);
                } else
                    Log.d(DATA_LOG_TAG, response.message());
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                //if (retrofitCurrentRetryCount < RETROFIT_RETRY_COUNT) {
                //    call.clone().enqueue(this);
                //    retrofitCurrentRetryCount++;
                //}
                swipeRefreshLayout.setRefreshing(false);
                Log.d(DATA_LOG_TAG, "failed");
            }
        };
    }

    public static Callback<Movies> getTopRatedMovies(final Realm realm
            , final String DATA_LOG_TAG
            , final int listType
            , final SwipeRefreshLayout swipeRefreshLayout) {
        return new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Log.d(DATA_LOG_TAG, "response");
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    Log.d(DATA_LOG_TAG, "success");
                    List<Movie> movies = response.body().getMovies();
                    Utilities.addMoviesToRealm(movies, listType, realm);
                } else
                    Log.d(DATA_LOG_TAG, response.message());
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                //if (retrofitCurrentRetryCount < RETROFIT_RETRY_COUNT) {
                //    call.clone().enqueue(this);
                //    retrofitCurrentRetryCount++;
                //}
                swipeRefreshLayout.setRefreshing(false);
                Log.d(DATA_LOG_TAG, "failed");
            }
        };
    }


    public static Callback<Reviews> getMovieReviewsCallBack(final String DATA_LOG_TAG, final Realm realm, final RealmObjectMovie movie) {
        return new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                Log.d(DATA_LOG_TAG, "reviews response");
                if (response.isSuccessful()) {
                    Log.d(DATA_LOG_TAG, "reviews success");
                    List<Review> reviews = response.body().getReviews();
                    addReviewsToRealm(reviews, realm, movie);
                } else
                    Log.d(DATA_LOG_TAG, response.message());
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                //if (retrofitCurrentRetryCount < RETROFIT_RETRY_COUNT) {
                //    call.clone().enqueue(this);
                //    retrofitCurrentRetryCount++;
                //}
                Log.d(DATA_LOG_TAG, "reviews failed");
            }
        };
    }

    public static Callback<Videos> getMovieVideosCallBack(final String DATA_LOG_TAG, final Realm realm, final RealmObjectMovie movie) {
        return new Callback<Videos>() {
            @Override
            public void onResponse(Call<Videos> call, Response<Videos> response) {
                Log.d(DATA_LOG_TAG, "videos response");
                if (response.isSuccessful()) {
                    Log.d(DATA_LOG_TAG, "videos success");
                    List<Video> videos = response.body().getVideos();
                    addVideosToRealm(videos, realm, movie);
                } else
                    Log.d(DATA_LOG_TAG, response.message());
            }

            @Override
            public void onFailure(Call<Videos> call, Throwable t) {
                //if (retrofitCurrentRetryCount < RETROFIT_RETRY_COUNT) {
                //    call.clone().enqueue(this);
                //    retrofitCurrentRetryCount++;
                //}
                Log.d(DATA_LOG_TAG, "videos failed");
            }
        };
    }

    public static Callback<Cast> getMovieCastCallBack(final String DATA_LOG_TAG, final Realm realm, final RealmObjectMovie movie) {
        return new Callback<Cast>() {
            @Override
            public void onResponse(Call<Cast> call, Response<Cast> response) {
                Log.d(DATA_LOG_TAG, "cast response");
                if (response.isSuccessful()) {
                    Log.d(DATA_LOG_TAG, "cast success");
                    List<CastMember> castMembers = response.body().getCastMembers();
                    addCastMembersToRealm(castMembers, realm, movie);
                } else
                    Log.d(DATA_LOG_TAG, response.message());
            }

            @Override
            public void onFailure(Call<Cast> call, Throwable t) {
                //if (retrofitCurrentRetryCount < RETROFIT_RETRY_COUNT) {
                //    call.clone().enqueue(this);
                //    retrofitCurrentRetryCount++;
                //}
                Log.d(DATA_LOG_TAG, "cast failed");
            }
        };
    }

    public static void addMoviesToRealm(List<Movie> movies, int listType, Realm realm) {
        for (Movie movie : movies) {
            final String title = movie.getTitle();
            final String posterPath = movie.getPosterPath();
            final String backdropPath = movie.getBackdropPath();
            final int id = movie.getId();
            final double popularity = movie.getPopularity();
            final double voteAverage = movie.getVoteAverage();
            final String overview = movie.getOverview();
            final String releaseDate = movie.getReleaseDate();
            realm.beginTransaction();
            RealmObjectMovie realmObjectMovie = realm.where(RealmObjectMovie.class)
                    .equalTo("id", id).equalTo("listType", listType).findFirst();
            if (realmObjectMovie == null) {
                //creating a new object if the movie isn't in the database at all or..
                //..if the movie is stored with another list type, to make sure lists that..
                //..are in more than one list are displayed in all of them
                realmObjectMovie = realm.createObject(RealmObjectMovie.class);
                realmObjectMovie.setUniqID((int) Utilities.getNextMovieUniqID(realm));
                realmObjectMovie.setFavorite(false);
                realmObjectMovie.setListType(listType);
            }
            realmObjectMovie.setTitle(title);
            realmObjectMovie.setPosterPath(posterPath);
            realmObjectMovie.setBackdropPath(backdropPath);
            realmObjectMovie.setId(id);
            realmObjectMovie.setPopularity(popularity);
            realmObjectMovie.setVoteAverage(voteAverage);
            realmObjectMovie.setOverview(overview);
            realmObjectMovie.setReleaseDate(releaseDate);
            realm.copyToRealmOrUpdate(realmObjectMovie);
            realm.commitTransaction();
        }
    }

    public static void addReviewsToRealm(List<Review> reviews, Realm realm, RealmObjectMovie movie) {
        for (Review review : reviews) {
            final String id = review.getId();
            final String author = review.getAuthor();
            final String content = review.getContent();
            final String url = review.getUrl();
            realm.beginTransaction();
            RealmObjectReview realmObjectReview = realm.where(RealmObjectReview.class)
                    .equalTo("id", id).findFirst();
            if (realmObjectReview == null) {
                realmObjectReview = realm.createObject(RealmObjectReview.class);
                realmObjectReview.setId(id);
            }
            realmObjectReview.setAuthor(author);
            realmObjectReview.setContent(content);
            realmObjectReview.setMovieID(movie.getId());
            realmObjectReview.setUrl(url);
            realm.commitTransaction();
        }
    }

    public static void addVideosToRealm(List<Video> videos, Realm realm, RealmObjectMovie movie) {
        for (Video video : videos) {
            final String id = video.getId();
            final String key = video.getKey();
            final String name = video.getName();
            final String site = video.getSite();
            final String type = video.getType();
            final int size = video.getSize();
            realm.beginTransaction();
            RealmObjectVideo realmObjectVideo = realm.where(RealmObjectVideo.class)
                    .equalTo("id", id).findFirst();
            if (realmObjectVideo == null) {
                realmObjectVideo = realm.createObject(RealmObjectVideo.class);
                realmObjectVideo.setId(id);
            }
            realmObjectVideo.setKey(key);
            realmObjectVideo.setName(name);
            realmObjectVideo.setSite(site);
            realmObjectVideo.setSize(size);
            realmObjectVideo.setType(type);
            realmObjectVideo.setMovieID(movie.getId());
            realmObjectVideo.setMovieTitle(movie.getTitle());
            realm.commitTransaction();
        }
    }

    public static void addCastMembersToRealm(List<CastMember> castMembers, Realm realm, RealmObjectMovie movie) {
        for (CastMember castMember : castMembers) {
            final int id = castMember.getId();
            final String character = castMember.getCharacter();
            final int order = castMember.getOrder();
            final int movieID = movie.getId();
            final String profilePath = castMember.getProfilePath();
            final String name = castMember.getName();
            realm.beginTransaction();
            RealmObjectCastMember realmObjectCastMember = realm.where(RealmObjectCastMember.class)
                    .equalTo("movieID", movieID).equalTo("id", id).findFirst();
            if (realmObjectCastMember == null) {
                //creating a new object if the person isn't in the database at all or..
                //..if the person is stored with another movie ID, to make sure persons that..
                //..are in more than one movie are displayed in all of them
                realmObjectCastMember = realm.createObject(RealmObjectCastMember.class);
                realmObjectCastMember.setUniqID(((int) Utilities.getNextCastMemberUniqID(realm)));
            }
            realmObjectCastMember.setName(name);
            realmObjectCastMember.setCharacter(character);
            realmObjectCastMember.setId(id);
            realmObjectCastMember.setOrder(order);
            realmObjectCastMember.setProfilePath(profilePath);
            realmObjectCastMember.setMovieID(movieID);
            realm.commitTransaction();
        }
    }

    //converts a DP unit to pixels
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

}
