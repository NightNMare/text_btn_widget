package com.naver.nightnmare.lunch_for_sunrin2;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("{school}/{schoolnum}")
    Call<JsonObject> getData(@Path("school") String school,@Path("schoolnum") String schoolnum,@Query("month") String month, @Query("hideAllergy") boolean hideAllergy);
}
