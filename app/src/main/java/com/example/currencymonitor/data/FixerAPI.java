package com.example.currencymonitor.data;

// Fixer API:
// http://fixer.io

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FixerAPI {
    @GET("latest")
    Single<MetaCurr> getData(@Query("base") String baseCurrency);
}