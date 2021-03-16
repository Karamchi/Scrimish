package com.example.scrimish;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Service {

    @GET("room-members")
    Call<Map<String, List<String>>> groupList();

    @GET("/rooms")
    Call<List<String>> rooms();

    @POST("/update")
    Call<Void> update(@Body RequestBody room);

    @POST("/remove")
    Call<Void> onDisconnect(@Body RequestBody room);
}
