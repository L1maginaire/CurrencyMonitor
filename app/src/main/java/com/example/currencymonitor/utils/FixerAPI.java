package com.example.currencymonitor.data;

// Fixer API:
// http://fixer.io

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FixerAPI /*extends Serializable */{
    @GET("latest")
    Single<MetaCurrency> getData(@Query("base") String baseCurrency);

    @GET("{date}")
    Single<MetaCurrency> statistics(@Path("date")String date, @Query("base")String base);
}