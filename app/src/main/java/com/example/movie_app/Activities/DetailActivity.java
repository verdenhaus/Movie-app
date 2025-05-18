package com.example.movie_app.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.movie_app.Adapters.ActorsListAdapter;
import com.example.movie_app.Adapters.CategoryEachFilmListAdapter;
import com.example.movie_app.Domian.FilmItem;
import com.example.movie_app.R;
import com.example.movie_app.User;
import com.example.movie_app.network.MovieApi;
import com.example.movie_app.network.RetrofitClient;
import com.example.movie_app.network.UserApi;

import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;


public class DetailActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView titleTxt, movieRateTxt, movieTimeTxt, movieSummaryInfo, movieActorsInfo;
    private int idFilm;
    private ImageView pic2, backImg, favImg;
    private RecyclerView.Adapter adapterActorList, adapterCategory;
    private RecyclerView recyclerViewActors, recyclerViewCategory;
    private NestedScrollView scrollView;
    private boolean isFavorite = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        idFilm = getIntent().getIntExtra("id", 0);
        initView();
        sendRequest();
    }

    private void sendRequest() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        MovieApi movieApi = RetrofitClient.getClient().create(MovieApi.class);
        Call<FilmItem> call = movieApi.getMovieDetail(idFilm);

        call.enqueue(new Callback<FilmItem>() {
            @Override
            public void onResponse(Call<FilmItem> call, Response<FilmItem> response) {
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null) {
                    FilmItem item = response.body();

                    Glide.with(DetailActivity.this)
                            .load(item.getPoster())
                            .into(pic2);
                    titleTxt.setText(item.getTitle());
                    movieRateTxt.setText(item.getImdbRating());
                    movieTimeTxt.setText(item.getRuntime());
                    movieSummaryInfo.setText(item.getPlot());
                    movieActorsInfo.setText(item.getActors());

                    if (item.getImages() != null) {
                        adapterActorList = new ActorsListAdapter(item.getImages());
                        recyclerViewActors.setAdapter(adapterActorList);
                    }
                    if (item.getGenres() != null) {
                        adapterCategory = new CategoryEachFilmListAdapter(item.getGenres());
                        recyclerViewCategory.setAdapter(adapterCategory);
                    }
                } else {
                    Log.e("MovieApi", "Response error: " + response.code());
                }

                SharedPreferences sharedPref = getSharedPreferences("MovieAppPrefs", MODE_PRIVATE);
                String username = sharedPref.getString("username", null);

                if (username != null) {
                    UserApi userApi = RetrofitClient.getClient().create(UserApi.class);
                    userApi.getUserByUsername(username).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Long userId = response.body().getId();

                                MovieApi movieApi = RetrofitClient.getClient().create(MovieApi.class);
                                movieApi.isFavorite(userId, idFilm).enqueue(new Callback<Boolean>() {
                                    @Override
                                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            isFavorite = response.body();
                                            Log.d("FAV_DEBUG", "API says favorite = " + isFavorite);
                                            updateFavIcon();
                                        } else {
                                            Log.e("FAV_DEBUG", "Unsuccessful response. Code: " + response.code());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Boolean> call, Throwable t) {
                                        Log.e("FAV_DEBUG", "Failed to check favorite", t);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.e("UserApi", "User fetch failed", t);
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<FilmItem> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("MovieApi", "Request failed", t);
            }
        });
    }


    private void initView() {
        titleTxt = findViewById(R.id.movieNameTxt);
        progressBar = findViewById(R.id.progressBarDetail);
        scrollView = findViewById(R.id.scrollView2);
        pic2 = findViewById(R.id.picDetail);
        movieRateTxt = findViewById(R.id.movieStar);
        movieTimeTxt = findViewById(R.id.movieTime);
        movieSummaryInfo = findViewById(R.id.movieSummary);
        movieActorsInfo = findViewById(R.id.movieActorInfo);
        backImg = findViewById(R.id.backImg);
        favImg = findViewById(R.id.favImg);
        recyclerViewActors = findViewById(R.id.imagesRecycle);
        recyclerViewCategory = findViewById(R.id.genreView);
        recyclerViewActors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        backImg.setOnClickListener(v -> finish());

        favImg.setOnClickListener(v -> {
            SharedPreferences sharedPref = getSharedPreferences("MovieAppPrefs", MODE_PRIVATE);
            String username = sharedPref.getString("username", null);

            if (username == null) {
                Toast.makeText(this, "Username not found. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            UserApi userApi = RetrofitClient.getClient().create(UserApi.class);
            userApi.getUserByUsername(username).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Long userId = response.body().getId();
                        MovieApi movieApi = RetrofitClient.getClient().create(MovieApi.class);

                        Call<Void> callFav = isFavorite
                                ? movieApi.removeFromFavorites(userId, idFilm)
                                : movieApi.addToFavorites(userId, idFilm);

                        favImg.setEnabled(false);
                        callFav.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                favImg.setEnabled(true);
                                if (response.isSuccessful()) {
                                    isFavorite = !isFavorite;
                                    Log.d("FAV_DEBUG", "Inside favImg.sendOnClickListener: isFavorite = " + isFavorite);
                                    updateFavIcon();
                                    Toast.makeText(DetailActivity.this,
                                            isFavorite ? "Added to favorites" : "Removed from favorites",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DetailActivity.this, "Operation failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                favImg.setEnabled(true);
                                Toast.makeText(DetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(DetailActivity.this, "Failed to retrieve user", Toast.LENGTH_SHORT).show();
                }
            });
        });


    }

    private void updateFavIcon() {
        Log.d("FAV_DEBUG", "Inside updateFavIcon: isFavorite = " + isFavorite);
        int color = isFavorite
                ? ContextCompat.getColor(this, android.R.color.holo_orange_light)
                : ContextCompat.getColor(this, android.R.color.white);
        favImg.setColorFilter(color);
    }
}