package com.gaspersoft.businvoice.api;

import com.gaspersoft.businvoice.ClsGlobal;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static IApiService apiService;

    public static IApiService GetService() {
        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ClsGlobal.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create(
                            new GsonBuilder().serializeNulls().create()
                    ))
                    .build();

            apiService = retrofit.create(IApiService.class);
        }
        return apiService;
    }
}
