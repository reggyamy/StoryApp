package com.reggya.stroryapp.di

import com.reggya.stroryapp.data.ApiConfig
import com.reggya.stroryapp.data.IRepository
import com.reggya.stroryapp.data.RemoteDataSource
import com.reggya.stroryapp.data.Repository

object Injection {

    private fun provideRepository(): IRepository {

        val remoteDataSource = RemoteDataSource.getInstance(ApiConfig.provideApiService())

        return Repository.getInstance(remoteDataSource)
    }

    fun provideUseCase(): IRepository {
        return provideRepository()
    }
}