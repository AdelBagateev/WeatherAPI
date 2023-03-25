package com.example.wetherapp.di

import com.example.wetherapp.BuildConfig
import com.example.wetherapp.data.WeatherRepositoryImpl
import com.example.wetherapp.data.interceptors.ApiKeyInterceptor
import com.example.wetherapp.data.interceptors.UnitsMetricInterceptor
import com.example.wetherapp.data.weather.datasource.remote.WeatherApi
import com.example.wetherapp.domain.weather.GetNearestWeatherUseCase
import com.example.wetherapp.domain.weather.GetWeatherUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DataContainer {

    private const val BASE_URL = BuildConfig.API_ENDPOINT




    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ApiKeyInterceptor())
            .addInterceptor(UnitsMetricInterceptor())
            .connectTimeout(10L, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherApi: WeatherApi = retrofit.create(WeatherApi::class.java)

    private val weatherRepository = WeatherRepositoryImpl(weatherApi)

    val weatherUseCase: GetWeatherUseCase
        get() = GetWeatherUseCase(weatherRepository)

    val nearestWeatherUseCase: GetNearestWeatherUseCase
        get() = GetNearestWeatherUseCase(weatherRepository)


}
