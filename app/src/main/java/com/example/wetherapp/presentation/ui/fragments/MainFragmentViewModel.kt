package com.example.wetherapp.presentation.ui.fragments

import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wetherapp.data.weather.datasource.remote.response.FindResponse
import com.example.wetherapp.di.DataContainer
import com.example.wetherapp.domain.weather.GetNearestWeatherUseCase
import com.example.wetherapp.domain.weather.GetWeatherUseCase
import com.example.wetherapp.presentation.ui.adapter.CityAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

class MainFragmentViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getNearestWeatherUseCase: GetNearestWeatherUseCase,

) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean>
        get() = _loading


    private val _weatherList = MutableLiveData<List<FindResponse>?>(null)
    val weatherList: LiveData<List<FindResponse>?>
        get() = _weatherList


    private val _error = MutableLiveData<String>(null)
    val error: LiveData<String>
        get() = _error


    val navigateToFragment = SingleLiveEvent<Int>()

    private val _adapter = MutableLiveData<CityAdapter>(null)
    val adapter: LiveData<CityAdapter>
        get() = _adapter


//    private val _idToNavigate = MutableLiveData<CityAdapter>(null)
//    val idToNavigate: LiveData<CityAdapter>
//        get() = _idToNavigate

//    private val _adapter = MutableLiveData<CityAdapter>(null)
//    val adapter: LiveData<CityAdapter>
//        get() = _adapter


    fun loadNearestWeather(lat : Double?, lon : Double?) {
        getNearestWeathers(lat, lon)
    }


    fun initAdapter() {
        _adapter.value = CityAdapter(emptyList()) {
            navigateToDetails(it)
        }
    }

    fun navigateToDetails(id : Int) {
        navigateToFragment.value = id
    }


    private fun getNearestWeathers(lat : Double?, lon : Double?) {
        viewModelScope.launch {
            try {
                _loading.value = true
                getNearestWeatherUseCase(lat.toString(), lon.toString(), 10.toString())
                    .also {
                        _weatherList.value = it.list
                    }
            } catch (er : Throwable) {
                _error.value = er.message
            } finally {
                _loading.value = false
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val weatherUseCase = DataContainer.weatherUseCase
                val nearestWeatherUseCase = DataContainer.nearestWeatherUseCase
                MainFragmentViewModel(weatherUseCase, nearestWeatherUseCase)
            }
        }
    }


    fun isExist(query: String)  {
        viewModelScope.launch {
            try {
                getWeatherUseCase(query).also {
                    withContext(Dispatchers.Main) {
                        navigateToFragment.value =  it.id
                    }
                }
            } catch (error: Throwable) {
                _error.value = error.message
            }
        }
    }


    class SingleLiveEvent<T> : MutableLiveData<T?>() {

        private val mPending = AtomicBoolean(false)

        override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
            super.observe(owner, Observer {
                if (mPending.compareAndSet(true, false)) {
                    observer.onChanged(it)
                }
            })
        }

        @MainThread
        override fun setValue(t: T?) {
            mPending.set(true)
            super.setValue(t)
        }
    }

}
