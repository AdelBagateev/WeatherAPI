package com.example.wetherapp.domain.weather

import com.example.wetherapp.data.weather.datasource.remote.response.WeatherResponse

class GetWeatherUseCase(
    private val weatherRepository: WeatherRepository
) {

    suspend operator fun invoke(
        query: String
    ): WeatherResponse = weatherRepository.getWeather(query)
}
