package com.example.movie_app.network;

import com.example.movie_app.Domian.FilmItem;
import com.example.movie_app.Domian.ListFilm;
import com.example.movie_app.Domian.GenresItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MovieApi {
    @GET("api/movies/best")
    Call<ListFilm> getBestMovies();

    @GET("api/movies/upcoming")
    Call<ListFilm> getUpcomingMovies();

    @GET("api/genres")
    Call<ArrayList<GenresItem>> getGenres();

    @GET("api/movies/{id}")
    Call<FilmItem> getMovieDetail(@Path("id") int movieId);

    @POST("api/users/{userId}/favorites/{movieId}")
    Call<Void> addToFavorites(@Path("userId") Long userId, @Path("movieId") int movieId);

    @GET("api/users/{userId}/favorites/{movieId}")
    Call<Boolean> isFavorite(@Path("userId") Long userId, @Path("movieId") int movieId);

    @DELETE("api/users/{userId}/favorites/{movieId}")
    Call<Void> removeFromFavorites(@Path("userId") Long userId, @Path("movieId") int movieId);

    @GET("/api/users/{userId}/favorites")
    Call<ListFilm> getFavoritesByUserId(@Path("userId") Long userId);


}
