package com.sourcey.materiallogindemo.service;

import com.sourcey.materiallogindemo.model.BlogPost;
import com.sourcey.materiallogindemo.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserClient {

    @FormUrlEncoded
    @POST("/api/auth/login/")
    Call<User> login(@Field("username") String username, @Field("password") String password);



    @GET("/api/postings")
    Call<List<BlogPost>> getPostings(@Header("Authorization") String authHeader);
}

