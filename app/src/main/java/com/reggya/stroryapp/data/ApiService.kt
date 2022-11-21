package com.reggya.stroryapp.data

import io.reactivex.rxjava3.core.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @POST("register")
    fun register(@Body registerResponse: RegisterResponse):Flowable<PostResponse>

    @POST("login")
    fun login(@Body registerResponse: RegisterResponse):Flowable<PostResponse>

    @GET("stories")
    fun getStories(@Header("Authorization") token: String): Flowable<StoriesResponse>

    @Multipart
    @POST("stories")
    fun addStories(@Header("Authorization") token: String,
                   @Part file: MultipartBody.Part,
                   @Part("description") description: RequestBody): Flowable<PostResponse>

}
