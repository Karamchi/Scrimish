package com.example.scrimish;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {

    private static final String BASE_URL = "https://api2.scaledrone.com/" + RoomManager.CHANNEL_ID + "/";//"https://scrimish.glitch.me/";
    private static final String MIME_TYPE_TEXT = "text/plain";

    public static Retrofit builder() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static RequestBody getRequestBody(String s) {
        return RequestBody.create(MediaType.parse(MIME_TYPE_TEXT), s);
    }



}
