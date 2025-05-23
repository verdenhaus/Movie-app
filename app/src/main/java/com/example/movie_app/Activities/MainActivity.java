package com.example.movie_app.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movie_app.Adapters.CategoryListAdapter;
import com.example.movie_app.Adapters.FilmListAdapter;
import com.example.movie_app.Adapters.SliderAdapters;
import com.example.movie_app.Domian.GenresItem;
import com.example.movie_app.Domian.ListFilm;
import com.example.movie_app.Domian.SliderItems;
import com.example.movie_app.network.*;
import com.example.movie_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private  RecyclerView.Adapter adapterBestMovies, AdapterUpComing, adapterCategory;
    private RecyclerView recyclerViewBestMovies, recyclerViewUpcoming, recyclerViewCategory;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest, mStringRequest2, mStringRequest3;
    private ProgressBar loading1, loading2, loading3;
    private ViewPager2 viewPager2;
    private Handler slideHandler = new Handler();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        banner();
        sendRequestBestMovies();
        sendRequestUpComing();
        sendRequestCategory();
    }

    private void sendRequestBestMovies() {
        loading1.setVisibility(View.VISIBLE);

        MovieApi movieApi = RetrofitClient.getClient().create(MovieApi.class);
        Call<ListFilm> call = movieApi.getBestMovies();

        call.enqueue(new Callback<ListFilm>() {
            @Override
            public void onResponse(Call<ListFilm> call, Response<ListFilm> response) {
                loading1.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapterBestMovies = new FilmListAdapter(response.body());
                    recyclerViewBestMovies.setAdapter(adapterBestMovies);
                } else {
                    Log.e("MovieApi", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ListFilm> call, Throwable t) {
                loading1.setVisibility(View.GONE);
                Log.e("MovieApi", "Request failed", t);
            }
        });

    }

    private void sendRequestUpComing() {
        loading3.setVisibility(View.VISIBLE);

        MovieApi movieApi = RetrofitClient.getClient().create(MovieApi.class);
        Call<ListFilm> call = movieApi.getUpcomingMovies();

        call.enqueue(new Callback<ListFilm>() {
            @Override
            public void onResponse(Call<ListFilm> call, Response<ListFilm> response) {
                loading3.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    AdapterUpComing = new FilmListAdapter(response.body());
                    recyclerViewUpcoming.setAdapter(AdapterUpComing);
                } else {
                    Log.e("MovieApi", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ListFilm> call, Throwable t) {
                loading3.setVisibility(View.GONE);
                Log.e("MovieApi", "Request failed", t);
            }
        });
    }


    private void sendRequestCategory() {
        loading2.setVisibility(View.VISIBLE);

        MovieApi movieApi = RetrofitClient.getClient().create(MovieApi.class);
        Call<ArrayList<GenresItem>> call = movieApi.getGenres();

        call.enqueue(new Callback<ArrayList<GenresItem>>() {
            @Override
            public void onResponse(Call<ArrayList<GenresItem>> call, Response<ArrayList<GenresItem>> response) {
                loading2.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapterCategory = new CategoryListAdapter(response.body());
                    recyclerViewCategory.setAdapter(adapterCategory);
                } else {
                    Log.e("MovieApi", "Genre response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<GenresItem>> call, Throwable t) {
                loading2.setVisibility(View.GONE);
                Log.e("MovieApi", "Genre request failed", t);
            }
        });
    }



    private void banner() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.wide1));
        sliderItems.add(new SliderItems(R.drawable.wide));
        sliderItems.add(new SliderItems(R.drawable.wide3));

        viewPager2.setAdapter(new SliderAdapters(sliderItems, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r*0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.setCurrentItem(1);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHandler.removeCallbacks(sliderRunnable);

            }
        });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideHandler.postDelayed(sliderRunnable, 2000);

    }

    private void initView(){
        viewPager2 = findViewById(R.id.viewpageSlider);

        recyclerViewBestMovies = findViewById(R.id.view1);
        recyclerViewBestMovies.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUpcoming = findViewById(R.id.view3);
        recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategory = findViewById(R.id.view2);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        loading1 = findViewById(R.id.progressBar1);
        loading2 = findViewById(R.id.progressBar2);
        loading3 = findViewById(R.id.progressBar3);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_explorer) {
                return true;
            } else if (itemId == R.id.menu_favourite) {
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                return true;
            } else if (itemId == R.id.menu_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });

    }

}