package ru.ekbtrees.treemap.data.retrofit

import okhttp3.*

class BaseAuthAuthenticatorInterceptor(
    private val authUrl: String,
    login: String,
    password: String
) : Interceptor, Authenticator, CookieJar {

    private val credential: String = Credentials.basic(login, password)
    private var accessToken: String? = null

    override fun authenticate(route: Route?, response: Response): Request {
        return buildAuthenticateRequest(response.request())
    }

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {

        if (accessToken == null) {
            proceed(buildAuthenticateRequest(request()))
        }

        proceed(
            if (accessToken != null) {
                request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()
            } else {
                request()
            }

        )
    }

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        accessToken = cookies
            .first { it.name().equals("AccessToken") }.value()
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> = mutableListOf()

    private fun buildAuthenticateRequest(request: Request): Request {
        return request.newBuilder()
            .url(authUrl)
            .method("POST", RequestBody.create(null, ""))
            .header("Authorization", credential)
            .build()
    }
}