package com.example.wetherapp.data

import com.example.wetherapp.data.weather.datasource.remote.WeatherApi
import com.example.wetherapp.data.weather.datasource.remote.response.WeatherResponse
import com.example.wetherapp.data.weather.datasource.remote.response.WrapperOfResponse
import com.example.wetherapp.domain.weather.WeatherRepository

class WeatherRepositoryImpl(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getWeather(
        query: String
    ): WeatherResponse = api.getWeather(query)

    override suspend fun getNearestWeather(
        lat: String,
        lon: String,
        count: String
    ): WrapperOfResponse = api.getNearestWeather(lat, lon, count)
}
