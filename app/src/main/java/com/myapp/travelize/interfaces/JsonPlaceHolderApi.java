package com.myapp.travelize.interfaces;

import com.myapp.travelize.models.Places;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    @GET("place/nearbysearch/json?")
    Call<Places.Root> doPlaces(@Query(value = "location", encoded = true) String location, @Query(value = "radius", encoded = true) String radius, @Query(value = "type", encoded = true) String type, @Query(value = "keyword", encoded = true) String keyword, @Query(value = "key", encoded = true) String key);
}
