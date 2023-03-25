package com.example.wetherapp.presentation.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherapp.R
import com.example.wetherapp.data.geolocation.GeoLocationDataSource
import com.example.wetherapp.presentation.ui.adapter.CityAdapter
import com.example.wetherapp.presentation.ui.adapter.SpaceItemDecorator
import com.example.wetherapp.databinding.FragmentMainBinding
import com.example.wetherapp.hideKeyboard
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainFragment : Fragment(R.layout.fragment_main) {
    private var binding: FragmentMainBinding? = null
    private val viewModel: MainFragmentViewModel by viewModels {
        MainFragmentViewModel.Factory
    }

    private var localAdapter : CityAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        viewModel.initAdapter()
        getCoordinates()
        observeViewModel()

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
            viewModel.isExist(query)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        with(viewModel) {

            loading.observe(viewLifecycleOwner) {
                binding?.progress?.isVisible = it
            }

            weatherList.observe(viewLifecycleOwner) {
                if(it == null) return@observe
                localAdapter?.list = it
                localAdapter?.notifyDataSetChanged()
            }

            error.observe(viewLifecycleOwner) {
                if (it == null) return@observe
                showError(it)
            }

            adapter.observe(viewLifecycleOwner) {
                localAdapter = it
                binding?.rvCities?.adapter = localAdapter
                binding?.rvCities?.addItemDecoration(SpaceItemDecorator(requireContext(), 16f))
                binding?.rvCities?.layoutManager = LinearLayoutManager(requireContext())
            }

            navigateToFragment.observe(viewLifecycleOwner) {id->
                id?.let { navigateToWeatherFragment(it)}
            }

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
            lifecycleScope.launch {
                val location = GeoLocationDataSource(userLocation).getLastLocation()
                viewModel.loadNearestWeather(location.lat, location.lon)
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
    private fun showError(errorMessage : String) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
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
