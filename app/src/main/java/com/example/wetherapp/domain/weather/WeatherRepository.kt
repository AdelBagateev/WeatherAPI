package com.example.wetherapp.domain.weather

import com.example.wetherapp.data.weather.datasource.remote.response.WeatherResponse
import com.example.wetherapp.data.weather.datasource.remote.response.WrapperOfResponse

interface WeatherRepository {
    suspend fun getWeather(query: String): WeatherResponse
    suspend fun getNearestWeather(lat: String, lon: String, count : String): WrapperOfResponse
}
