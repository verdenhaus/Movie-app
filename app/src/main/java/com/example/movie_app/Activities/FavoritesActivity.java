package com.example.movie_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

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
        mRequestQueue = Volley.newRequestQueue(this);
        loading.setVisibility(View.VISIBLE);

        mStringRequest = new StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies?page=1",
                response -> {
                    loading.setVisibility(View.GONE);
                    ListFilm items = new Gson().fromJson(response, ListFilm.class);
                    adapterFavorites = new FilmListAdapter(items);
                    recyclerViewFavorites.setAdapter(adapterFavorites);
                },
                error -> {
                    loading.setVisibility(View.GONE);
                });

        mRequestQueue.add(mStringRequest);
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