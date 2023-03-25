package com.example.wetherapp.domain.weather

import com.example.wetherapp.data.weather.datasource.remote.response.WrapperOfResponse

class GetNearestWeatherUseCase(
    private val weatherRepository: WeatherRepository
) {

    suspend operator fun invoke(
        lat: String,
        lon: String,
        count : String
    ): WrapperOfResponse = weatherRepository.getNearestWeather(lat, lon, count)
}
