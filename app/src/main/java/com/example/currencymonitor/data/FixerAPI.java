package com.example.currencymonitor.data;

// Fixer API:
// http://fixer.io

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FixerAPI /*extends Serializable */{
    @GET("latest")
    Single<MetaCurr> getData(@Query("base") String baseCurrency);

    @GET("{date}")
    Single<MetaCurr> statistics(@Path("date")String date, @Query("base")String base);
}