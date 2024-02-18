package com.example.cryptoapp.data.network

import com.example.pizzastore.data.network.model.AddressResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("geocode?locale=ru&key=30032ecb-8a07-429d-b694-51fe8f3f0d14&reverse=true")
    suspend fun getAddress(
        @Query(QUERY_PARAM_API_POINT) point: String
    ): AddressResponseDto

    companion object {
        private const val QUERY_PARAM_API_POINT = "point"
    }
}