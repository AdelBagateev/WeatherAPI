package com.example.wetherapp.presentation.ui.adapter

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wetherapp.data.weather.datasource.remote.response.FindResponse
import com.example.wetherapp.databinding.ItemCityBinding

class CityItem(
    private val binding: ItemCityBinding,
    private val action: (Int) -> Unit,
) :  RecyclerView.ViewHolder(binding.root) {

    fun onBind(city: FindResponse) {
        with(binding) {
            tvName.text = city.name
            tvTemp.text = city.main.temp.toString()
            tvTemp.setTextColor(calculateColor(city.main.temp.toInt()))
            ivIcon.load("https://openweathermap.org/img/w/${city.weather[0].icon}.png")


            root.setOnClickListener {
                action(city.id)
            }
        }
    }

    private fun calculateColor(temp : Int) : Int {
        var color : Int ?= null
        if(temp < -30) {
            color = Color.BLUE
        } else
        if(temp<0) {
            color = Color.CYAN
        } else
        if(temp == 0) {
            color = Color.GREEN
        } else
        if(temp<10) {
            color = Color.YELLOW
        } else
        if(temp>10) {
            color = Color.RED
        }
        return color ?: 0
    }
}
