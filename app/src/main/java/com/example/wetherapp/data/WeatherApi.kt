package com.example.wetherapp.data

import com.example.wetherapp.data.response.WeatherResponse
import com.example.wetherapp.data.response.WrapperOfResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather")
    suspend fun getWeather(
        @Query("q") city: String,
    ): WeatherResponse

    @GET("find")
    suspend fun getNearestWeather(
        @Query("lat") latOfLocation: String,
        @Query("lon") lonOfLocation: String,
        @Query("cnt") count: String
    ): WrapperOfResponse

    @GET("weather")
    suspend fun getWeatherById(
        @Query("id") id: String,
    ): WeatherResponse

}
