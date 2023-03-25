package com.example.wetherapp.presentation.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.wetherapp.R
import com.example.wetherapp.di.DataContainer
import com.example.wetherapp.databinding.FragmentWetherBinding
import kotlinx.coroutines.launch

class WeatherFragment : Fragment(R.layout.fragment_wether) {
    private val api = DataContainer.weatherApi

    private var binding : FragmentWetherBinding ?= null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val cityId = arguments?.getInt(ARG_NAME).toString()
        binding = FragmentWetherBinding.bind(view)
        loadWeather(cityId)


        super.onViewCreated(view, savedInstanceState)
    }



    private fun loadWeather(id: String) {
        lifecycleScope.launch(){
            try {
                showLoading(true)
                api.getWeatherById(id).also {
                    it.weather.firstOrNull()?.run {
                        binding?.run {
                            tvName.text = it.name
                            tvWeatherDescription.text = description
                            tvMainHumidity.text =getString(R.string.humidity,   it.main.humidity.toString() )

                            tvVisibility.text = getString(R.string.visibilty,  it.visibility.toString())
                            ivIcon.load("https://openweathermap.org/img/w/${icon}.png")
                            tvDegree.text = getString(R.string.degree,  it.main.temp.toString())
                        }
                    }
                }

            } catch (error: Throwable) {
                showError()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isShow: Boolean) {
        binding?.progress?.isVisible = isShow
    }

    private fun showError() {
        Toast.makeText(requireContext(), "Что-то пошло не так...\nПопробуйте позже", Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val ARG_NAME = "id"
        fun newInstance(id: Int) = WeatherFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_NAME, id)
            }
        }
    }
}
