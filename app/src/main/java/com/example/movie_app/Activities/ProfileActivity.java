package com.example.movie_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movie_app.R;

public class ProfileActivity extends AppCompatActivity {
    private TextView emailView;
    private TextView userView;
    private Button logoutBtn;
    private LinearLayout dashboardBtn;

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
        dashboardBtn = findViewById(R.id.dashboardBtn);

        logoutBtn.setOnClickListener(v -> logBtnClick());
        dashboardBtn.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        });
    }

    public void logBtnClick(){

    }

}
