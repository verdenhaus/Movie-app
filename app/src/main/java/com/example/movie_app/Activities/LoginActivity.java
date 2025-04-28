package com.example.movie_app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movie_app.R;
import com.example.movie_app.User;
import com.example.movie_app.network.RetrofitClient;
import com.example.movie_app.network.UserApi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText userEdt, passEdt;
    private Button loginBtn;
    private TextView registBtn;

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
        registBtn = findViewById(R.id.registBtn);

        loginBtn.setOnClickListener(v -> logBtnClick());
        registBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        });
    }

    public void logBtnClick() {
        String username = userEdt.getText().toString().trim();
        String password = passEdt.getText().toString().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        UserApi userApi = RetrofitClient.getClient().create(UserApi.class);
        User user = new User(username, password, null); // email not needed here

        Call<ResponseBody> call = userApi.loginUser(user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String msg = response.body().string();
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();

                        if (msg.toLowerCase().contains("success")) {
                            SharedPreferences sharedPref = getSharedPreferences("MovieAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("username", username);
                            editor.putString("email", "user@email.com"); // Replace with actual email if available
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        String error = "";
                        if (response.errorBody() != null) {
                            error = response.errorBody().string();
                        }
                        Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "API Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
