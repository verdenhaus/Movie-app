package com.example.movie_app.network;

import com.example.movie_app.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {
    @POST("api/users/register")
    Call<ResponseBody> registerUser(@Body User user);

    @POST("/api/users/login")
    Call<User> loginUser(@Body User user);

}
