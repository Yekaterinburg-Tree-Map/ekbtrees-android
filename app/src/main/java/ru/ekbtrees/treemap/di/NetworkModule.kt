package ru.ekbtrees.treemap.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ekbtrees.treemap.data.api.TreesApiService
import javax.inject.Singleton

/**
 * Dagger модуль слоя работы с сетью
 * */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://ekb-trees-help.ru/"

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideTreesApiService(retrofit: Retrofit): TreesApiService {
        return retrofit.create(TreesApiService::class.java)
    }
}