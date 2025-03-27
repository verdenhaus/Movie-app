package com.example.movie_app.Activites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movie_app.R;


public class LoginActivity extends AppCompatActivity {

    private EditText userEdt, passEdt;
    private Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

    }

    private void initView() {
        userEdt = findViewById(R.id.editTextText);
        passEdt = findViewById(R.id.editTextPasswors);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v -> {
            if(userEdt.getText().toString().isEmpty() ||
                    passEdt.getText().toString().isEmpty()){

                Toast.makeText(LoginActivity.this, "Fill username and password", Toast.LENGTH_SHORT).show();

            } else if (userEdt.getText().toString().equals("test") &&
                    passEdt.getText().toString().equals("test")) {

                startActivity(new Intent(LoginActivity.this, MainActivity.class));

            }else {

                Toast.makeText(LoginActivity.this, "Incorrect username and password", Toast.LENGTH_SHORT).show();

            }
        });
    }
}