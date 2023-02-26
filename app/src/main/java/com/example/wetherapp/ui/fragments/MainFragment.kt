package com.example.wetherapp.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherapp.R
import com.example.wetherapp.adapter.CityAdapter
import com.example.wetherapp.adapter.SpaceItemDecorator
import com.example.wetherapp.data.DataContainer
import com.example.wetherapp.databinding.FragmentMainBinding
import com.example.wetherapp.hideKeyboard
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment(R.layout.fragment_main) {
    private var binding: FragmentMainBinding? = null
    private val api = DataContainer.weatherApi
    private var lat : Double?=null
    private var lon : Double?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        getCoordinates()

        binding?.run {
            swCity.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    if (query.isNotEmpty()) {
                        loadWeather(query)
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
    }

    private  fun loadWeather(query : String)  {
        binding?.run {
            swCity.hideKeyboard()
            isExist(query)
        }
    }


    private fun getCoordinates(){
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_DENIED) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )

            requestPermissions(permissions, REQUEST_CODE)
        } else {
            val userLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
            userLocation.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lon = location.longitude
                    lat = location.latitude
                    getNearestWeathers(lat, lon)
                } else {
                    Snackbar.make(binding!!.root, "Default location", Snackbar.LENGTH_LONG)
                        .show()
                    lat = 54.314192
                    lon = 48.403132
                    getNearestWeathers(lat, lon)
                }
            }
        }
    }



    private fun getNearestWeathers(lat : Double?, lon : Double?) {
        lifecycleScope.launch {
            try {
                showLoading(true)
                api.getNearestWeather(lat.toString(), lon.toString(), 10.toString())
                    .also {
                        binding?.run {
                            rvCities.adapter = CityAdapter(it.list) { city ->
                                navigateToWeatherFragment(city.id)
                            }
                            rvCities.addItemDecoration(SpaceItemDecorator(requireContext(), 16f))
                            rvCities.layoutManager = LinearLayoutManager(requireContext())
                        }
                    }
            } catch (error: Throwable) {
                showError()
            } finally {
                showLoading(false)
            }
        }
    }


    private fun isExist(query: String)  {
        lifecycleScope.launch {
            try {
                api.getWeather(query).also {
                    withContext(Dispatchers.Main) {
                        navigateToWeatherFragment(it.id)
                    }
                }
            } catch (error: Throwable) {
                showError()
            }
        }

    }

    private fun navigateToWeatherFragment(id : Int) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_cont, WeatherFragment.newInstance(id),"MAIN_FRAG")
            .addToBackStack("TRANSACTION")
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out,
            )
            .commit()
    }

    private fun showLoading(isShow: Boolean) {
        binding?.progress?.isVisible = isShow
    }

    private fun showError() {
        Toast.makeText(requireContext(), "Что-то пошло не так...\nПопробуйте позже", Toast.LENGTH_LONG).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCoordinates()
                } else {
                    Snackbar.make(binding!!.root, "Give permission in settings", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 1
    }
}
