package com.reggya.stroryapp.data

import io.reactivex.rxjava3.core.Flowable
import java.io.File

class Repository private constructor(
    private val remoteDataSource: RemoteDataSource
): IRepository{
    override fun register(registerResponse: RegisterResponse): Flowable<ApiResponse<PostResponse>> {
        return remoteDataSource.register(registerResponse)
    }

    override fun login(registerResponse: RegisterResponse): Flowable<ApiResponse<PostResponse>> {
        return remoteDataSource.login(registerResponse)
    }

    override fun getStories(token: String): Flowable<ApiResponse<StoriesResponse>> {
        return remoteDataSource.getStories(token)
    }

    override fun addStories(token: String, file: File, description: String): Flowable<ApiResponse<PostResponse>> {
        return remoteDataSource.addStories(token, file, description)
    }

    companion object{
        @Volatile
        private var instance: Repository? = null

        fun getInstance(
            remoteDataSource: RemoteDataSource
        ): Repository =
            instance ?: synchronized(this){
                instance ?: Repository(remoteDataSource)
            }
    }
}