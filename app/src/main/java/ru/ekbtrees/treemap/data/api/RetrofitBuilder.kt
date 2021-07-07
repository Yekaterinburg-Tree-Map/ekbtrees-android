package ru.ekbtrees.treemap.data.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.ekbtrees.treemap.domain.entity.ClusterTreesEntity

object RetrofitBuilder {

    private const val BASE_URL = "https://ekb-trees-help.ru/"

    private fun createGsonConverter() {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(ClusterTreesEntity::class.java, ClustersDeserializer())
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val treesApiService: TreesApiService = buildRetrofit().create(TreesApiService::class.java)
}