package com.afterapps.movies.helpers;

import com.afterapps.movies.datamodel.Cast;
import com.afterapps.movies.datamodel.Movies;
import com.afterapps.movies.datamodel.MoviesByPerson;
import com.afterapps.movies.datamodel.Person;
import com.afterapps.movies.datamodel.Reviews;
import com.afterapps.movies.datamodel.Videos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/*
 * Created by Mahmoud.AlyuDeen on 7/24/2016.
 */
public class RetrofitClients {

    public interface AllMoviesClient {
        //endpoints for the top 3 lists
        String topRatedEndPoint = "movie/top_rated";
        String popularEndPoint = "movie/popular";
        String upcomingEndPoint = "movie/upcoming";

        @GET(topRatedEndPoint)
        Call<Movies> getTopRatedMovies(
                @Query("api_key") String apiKey
        );

        @GET(popularEndPoint)
        Call<Movies> getPopularMovies(
                @Query("api_key") String apiKey
        );

        @GET(upcomingEndPoint)
        Call<Movies> getUpcomingMovies(
                @Query("api_key") String apiKey
        );
    }

    public interface MovieDetailsClient {
        //endpoints for movies' details
        String movieVideosEndPoint = "movie/{movieID}/videos";
        String movieReviewsEndPoint = "movie/{movieID}/reviews";
        String movieCastEndPoint = "movie/{movieID}/credits";

        @GET(movieVideosEndPoint)
        Call<Videos> getMovieVideos(
                @Path("movieID") int movieID,
                @Query("api_key") String apiKey
        );

        @GET(movieReviewsEndPoint)
        Call<Reviews> getMovieReviews(
                @Path("movieID") int movieID,
                @Query("api_key") String apiKey
        );

        @GET(movieCastEndPoint)
        Call<Cast> getMovieCast(
                @Path("movieID") int movieID,
                @Query("api_key") String apiKey
        );
    }

    public interface PersonDetailsClient {
        //endpoints for persons' details
        String personDetailsEndPoint = "person/{personID}";
        String knownForEndPoint = "person/{personID}/movie_credits";

        @GET(personDetailsEndPoint)
        Call<Person> getPersonDetails(
                @Path("personID") int personID,
                @Query("api_key") String apiKey
        );

        @GET(knownForEndPoint)
        Call<MoviesByPerson> getKnownFor(
                @Path("personID") int personID,
                @Query("api_key") String apiKey
        );
    }
}
