package com.example.pizzastore.data.network

import com.example.pizzastore.data.network.model.AddressResponseDto
import com.example.pizzastore.data.network.model.PathResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("geocode?locale=ru&reverse=true")
    suspend fun getAddress(
        @Query(QUERY_PARAM_API_POINT) point: String
    ): AddressResponseDto

    @GET("route?")
    suspend fun getPath(
        @Query(QUERY_PARAM_API_POINTS) point1: String,
        @Query(QUERY_PARAM_API_POINTS) point2: String
    ): Response<PathResponseDto>

    companion object {
        private const val QUERY_PARAM_API_POINT = "point"
        private const val QUERY_PARAM_API_POINTS = "points"
    }
}