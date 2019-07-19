package com.example.hereiammccauleyj;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface JsonPlaceHolderApi {

    @GET
    Call<TimeZoneData> getTimeZone(@Url String url);
}
