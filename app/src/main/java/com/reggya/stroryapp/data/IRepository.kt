package com.reggya.stroryapp.data

import io.reactivex.rxjava3.core.Flowable
import java.io.File

interface IRepository {

    fun register(registerResponse: RegisterResponse): Flowable<ApiResponse<PostResponse>>
    fun login(registerResponse: RegisterResponse): Flowable<ApiResponse<PostResponse>>
    fun getStories(token: String): Flowable<ApiResponse<StoriesResponse>>
    fun addStories(token: String, file: File, description: String): Flowable<ApiResponse<PostResponse>>
}
