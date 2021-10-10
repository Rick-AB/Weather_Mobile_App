package com.example.weather_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_app.model.WeatherResult
import com.example.weather_app.repository.WeatherListRepository

class MainActivityViewModel : ViewModel() {
    private var weatherList: MutableLiveData<List<WeatherResult>> = MutableLiveData(ArrayList())
    private lateinit var weatherListRepository: WeatherListRepository
    private var sortBy: String = "asc"

    fun init() {
        weatherListRepository = WeatherListRepository.getInstance()
        refreshWeatherList()
    }

    fun getWeatherList(): LiveData<List<WeatherResult>> {
        return weatherList
    }

    fun refreshWeatherList() {
        weatherList = weatherListRepository.getWeatherDetail()
    }

    fun getIsRefreshing(): LiveData<Boolean> {
        return weatherListRepository.isRefreshing
    }

    fun sortByCity(sortBy: String) {
        val list = weatherList.value!!
        if (sortBy == "asc") {
            val sortedList = list.sortedBy { it.name }
            weatherList.postValue(sortedList)
        } else {
            val sortedList = list.sortedByDescending { it.name }
            weatherList.postValue(sortedList)
        }
    }

    fun sortByTemp(sortBy: String) {
        val list = weatherList.value!!
        if (sortBy == "asc") {
            val sortedList = list.sortedBy { it.main.temp }
            weatherList.postValue(sortedList)
        } else {
            val sortedList = list.sortedByDescending { it.main.temp }
            weatherList.postValue(sortedList)
        }
    }
}