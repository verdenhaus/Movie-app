package com.example.movie_app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movie_app.Adapters.FilmListAdapter;
import com.example.movie_app.Domian.ListFilm;
import com.example.movie_app.R;
import com.example.movie_app.User;
import com.example.movie_app.network.MovieApi;
import com.example.movie_app.network.RetrofitClient;
import com.example.movie_app.network.UserApi;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterFavorites;
    private RecyclerView recyclerViewFavorites;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initView();
        sendRequestFavorites();
        setupBottomNavigation();
    }

    private void sendRequestFavorites() {
        loading.setVisibility(View.VISIBLE);

        SharedPreferences sharedPref = getSharedPreferences("MovieAppPrefs", MODE_PRIVATE);
        String username = sharedPref.getString("username", null);

        if (username == null) {
            Toast.makeText(this, "Username not found. Please log in again.", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.GONE);
            return;
        }

        UserApi userApi = RetrofitClient.getClient().create(UserApi.class);
        userApi.getUserByUsername(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long userId = response.body().getId();

                    MovieApi movieApi = RetrofitClient.getClient().create(MovieApi.class);
                    Call<ListFilm> callFavorites = movieApi.getFavoritesByUserId(userId);
                    callFavorites.enqueue(new Callback<ListFilm>() {
                        @Override
                        public void onResponse(Call<ListFilm> call, Response<ListFilm> response) {
                            loading.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                adapterFavorites = new FilmListAdapter(response.body());
                                recyclerViewFavorites.setAdapter(adapterFavorites);
                            } else {
                                Log.e("MovieApi", "Favorites response error: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<ListFilm> call, Throwable t) {
                            loading.setVisibility(View.GONE);
                            Log.e("MovieApi", "Favorites request failed", t);
                        }
                    });
                } else {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(FavoritesActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(FavoritesActivity.this, "Failed to get user info", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initView() {
        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        loading = findViewById(R.id.progressBarFavorites);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menu_favourite);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_explorer) {
                startActivity(new Intent(FavoritesActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.menu_favourite) {
                return true;
            } else if (itemId == R.id.menu_profile) {
                startActivity(new Intent(FavoritesActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}