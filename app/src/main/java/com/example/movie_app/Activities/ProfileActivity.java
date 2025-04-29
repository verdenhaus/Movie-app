package com.example.movie_app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movie_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {
    private TextView emailView;
    private TextView userView;
    private Button logoutBtn;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
    }

    private void initView() {
        emailView = findViewById(R.id.emailView);
        userView = findViewById(R.id.usernameView);
        logoutBtn = findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(v -> logBtnClick());


        SharedPreferences sharedPref = getSharedPreferences("MovieAppPrefs", MODE_PRIVATE);
        String savedUsername = sharedPref.getString("username", "Unknown User");
        String savedEmail = sharedPref.getString("email", "Unknown Email");

        userView.setText(savedUsername);
        emailView.setText(savedEmail);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_explorer) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.menu_favourite) {
                startActivity(new Intent(ProfileActivity.this, FavoritesActivity.class));
                return true;
            } else if (itemId == R.id.menu_profile) {
                return true;
            }
            return false;
        });
    }


    public void logBtnClick() {
        getSharedPreferences("MovieAppPrefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();


        Intent intent = new Intent(ProfileActivity.this, IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}
