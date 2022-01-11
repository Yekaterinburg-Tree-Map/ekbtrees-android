package ru.ekbtrees.treemap.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ekbtrees.treemap.BuildConfig
import ru.ekbtrees.treemap.data.api.TreesApiService
import ru.ekbtrees.treemap.data.retrofit.BaseAuthAuthenticatorInterceptor
import ru.ekbtrees.treemap.data.retrofit.ResultAdapterFactory
import javax.inject.Singleton


/**
 * Dagger модуль слоя работы с сетью
 * */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://ekb-trees-help.ru/api/"
    private const val BASE_AUTH_URL = "https://ekb-trees-help.ru/auth/login"

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    fun provideAuthenticator(): BaseAuthAuthenticatorInterceptor =
        BaseAuthAuthenticatorInterceptor(
            BASE_AUTH_URL,
            // login и password класть в local.properties
            BuildConfig.login,
            BuildConfig.password
        )

    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authenticator: BaseAuthAuthenticatorInterceptor
    ): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(authenticator)
            .authenticator(authenticator)
            .cookieJar(authenticator)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }


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
}