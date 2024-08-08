package com.example.pizzastore.data.network

import com.example.pizzastore.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

    private const val BASE_URL = "https://graphhopper.com/api/1/"
    private const val KEY_PARAM = "key"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->

            val originalRequest = chain.request()

            val newUrl = originalRequest
                .url
                .newBuilder()
                .addQueryParameter(KEY_PARAM, BuildConfig.GEO_API_KEY)
                .build()

            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .build()

            chain.proceed(newRequest)

        }.build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
