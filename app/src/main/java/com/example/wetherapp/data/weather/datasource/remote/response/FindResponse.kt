package com.example.wetherapp.data.weather.datasource.remote.response

import com.google.gson.annotations.SerializedName

data class WrapperOfResponse (
    val message:String,
    val cod: String,
    val count: Int,
    val list : List<FindResponse>
    )

data class FindResponse(
    @SerializedName("dt")
    val dt: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: Main,
    @SerializedName("name")
    val name: String,
    @SerializedName("weather")
    val weather: List<Weather>,
)
