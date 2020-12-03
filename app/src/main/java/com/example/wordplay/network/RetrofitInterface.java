package com.example.wordplay.network;

import com.example.wordplay.models.Response;
import com.example.wordplay.models.User;

import rx.Observable;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RetrofitInterface {

    @POST("/users/")
    Observable<Response> register(@Body User user);

    @POST("/users/login/")
    Observable<Response> login();

    @POST("/users/login/")
    Observable<Response> login(@Body User user);

    @GET("/users/{login}")
    Observable<User> getProfile(@Path("login") String login);

    @PUT("users/{login}")
    Observable<Response> changePassword(@Path("login") String login, @Body User user);

    @POST("/api/v1/users/")
    Observable<Response> resetPasswordInit();

    @POST("/api/v1/users/login/")
    Observable<Response> resetPasswordFinish();
}
