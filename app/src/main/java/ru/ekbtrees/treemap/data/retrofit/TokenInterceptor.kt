package ru.ekbtrees.treemap.data.retrofit

import okhttp3.Interceptor
import okhttp3.Response
import ru.ekbtrees.treemap.BuildConfig
import javax.inject.Inject

class TokenInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val token: String = BuildConfig.access_token
        proceed(
            request()
                .newBuilder()
                .addHeader("Authorization", "Basic $token")
                .build()
        )
    }
}