package com.example.movie_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegistrationActivity extends AppCompatActivity {

    private EditText userEdt, passEdt, emailEdt;
    private Button signinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userEdt = findViewById(R.id.editTextText);
        passEdt = findViewById(R.id.editTextPassword);
        emailEdt = findViewById(R.id.editTextEmail);
        signinBtn = findViewById(R.id.signinBtn);
    }

    public void regBtnClick(View view) {
        String username = userEdt.getText().toString().trim();
        String password = passEdt.getText().toString().trim();
        String email = emailEdt.getText().toString().trim();

        // Empty field check
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Password strength (optional: adjust this rule)
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // All validation passed, proceed with API call
        UserApi userApi = RetrofitClient.getClient().create(UserApi.class);
        User user = new User(username, password, email);
        Call<ResponseBody> call = userApi.registerUser(user);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String msg = response.body().string();
                        Toast.makeText(RegistrationActivity.this, msg, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = "";
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                        Toast.makeText(RegistrationActivity.this,
                                "Registration failed: " + errorMsg,
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RegistrationActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(RegistrationActivity.this, "API Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
