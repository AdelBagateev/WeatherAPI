package com.example.wetherapp.data.interceptors

import com.example.wetherapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class UnitsMetricInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val newURL = original.url.newBuilder()
            .addQueryParameter("units", BuildConfig.UNITS)
            .build()
        return chain.proceed(
            original.newBuilder()
                .url(newURL)
                .build()
        )
    }
}
