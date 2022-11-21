package com.reggya.stroryapp.data

import android.util.Log
import com.google.gson.JsonParser
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class RemoteDataSource private constructor(private val apiService: ApiService){

    fun register(registerResponse: RegisterResponse): Flowable<ApiResponse<PostResponse>>{
        val result = PublishSubject.create<ApiResponse<PostResponse>>()

        val client = apiService.register(registerResponse)
        client.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .take(1)
            .subscribe({
                result.onNext(
                    if (it.error == false) ApiResponse.success(it)
                    else ApiResponse.error(it.message)
                )
            },{
                val error = it as retrofit2.HttpException
                val errorBody = error.response()?.errorBody()
                val parse = JsonParser().parse(errorBody.toString()).asString
                result.onNext(ApiResponse.error(parse.toString()))
                Log.e("Post register", it.toString())
            })

        return result.toFlowable(BackpressureStrategy.BUFFER)
    }

    fun login(registerResponse: RegisterResponse): Flowable<ApiResponse<PostResponse>>{
        val result = PublishSubject.create<ApiResponse<PostResponse>>()

        val client = apiService.login(registerResponse)
        client.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .take(1)
            .subscribe({
                result.onNext(
                    if (it.error == false) ApiResponse.success(it)
                    else ApiResponse.error(it.message)
                )
            },{
                val error = it as retrofit2.HttpException
                val errorBody= error.response()?.errorBody()
                result.onNext(ApiResponse.error(errorBody?.toString()))
                Log.e("Post login", it.toString())
            })
        return result.toFlowable(BackpressureStrategy.BUFFER)
    }

    fun getStories(token: String): Flowable<ApiResponse<StoriesResponse>>{
        val result = PublishSubject.create<ApiResponse<StoriesResponse>>()

        val client = apiService.getStories("Bearer $token")
        client.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .take(1)
            .subscribe({
                result.onNext(
                    if (it.error == false) ApiResponse.success(it)
                    else ApiResponse.empty(it)
                )
            },{
                val error = it as retrofit2.HttpException
                val errorBody = error.response()?.errorBody()?.string()
                result.onNext(ApiResponse.error(errorBody))
                Log.e("getStories", it.message.toString())
            })

        return result.toFlowable(BackpressureStrategy.BUFFER)
    }

    fun addStories(token: String, file: File, description: String):Flowable<ApiResponse<PostResponse>>{
        val result = PublishSubject.create<ApiResponse<PostResponse>>()

        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val image = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)
        val description: RequestBody = description.toRequestBody("text/plain".toMediaType())

        val client  = apiService.addStories("Bearer $token", image, description)
        client.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .take(1)
            .subscribe ({
                result.onNext(
                    if (it.error == false) ApiResponse.success(it)
                    else ApiResponse.empty(it)
                )
            },{
                result.onNext(ApiResponse.error(it.message))
                Log.e("postStory", it.toString())
            })

        return result.toFlowable(BackpressureStrategy.BUFFER)
    }

    companion object{
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance(apiService: ApiService): RemoteDataSource =
            instance ?: synchronized(this){
                instance ?: RemoteDataSource(apiService)
            }

    }
}