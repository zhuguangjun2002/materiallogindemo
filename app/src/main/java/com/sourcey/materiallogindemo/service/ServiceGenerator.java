package com.sourcey.materiallogindemo.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("http://192.168.1.106:8000")
            .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();


    public static <S> S createService(Class<S> serviceClass){
        return retrofit.create(serviceClass);

    }
}

