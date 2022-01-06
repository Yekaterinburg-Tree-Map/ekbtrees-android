package ru.ekbtrees.treemap.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ekbtrees.treemap.constants.NetworkConstants.BASE_URL
import ru.ekbtrees.treemap.data.api.TreesApiService
import ru.ekbtrees.treemap.data.files.FilesRepositoryImpl
import ru.ekbtrees.treemap.data.retrofit.ResultAdapterFactory
import ru.ekbtrees.treemap.data.retrofit.TokenInterceptor
import ru.ekbtrees.treemap.domain.repositories.FilesRepository
import javax.inject.Singleton

/**
 * Dagger модуль слоя работы с сетью
 * */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    fun provideTokenInterceptor(): TokenInterceptor {
        return TokenInterceptor()
    }

    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        tokenInterceptor: TokenInterceptor
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addCallAdapterFactory(ResultAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideTreesApiService(retrofit: Retrofit): TreesApiService =
        retrofit.create(TreesApiService::class.java)

    @Singleton
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO)
    }

    @Provides
    fun provideFilesRepository(
        @ApplicationContext context: Context,
        coroutineScope: CoroutineScope
    ): FilesRepository {
        return FilesRepositoryImpl(
            context = context as Application,
            coroutineScope = coroutineScope
        )
    }
}