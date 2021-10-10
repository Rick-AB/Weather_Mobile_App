package com.example.weather_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather_app.model.WeatherResult
import com.example.weather_app.repository.WeatherListRepository

class MainActivityViewModel : ViewModel() {
    private var weatherList: MutableLiveData<List<WeatherResult>> = MutableLiveData(ArrayList())
    private lateinit var weatherListRepository: WeatherListRepository

    //create instance of repo and fetch initial data
    fun init() {
        weatherListRepository = WeatherListRepository.getInstance()
        refreshWeatherList()
    }

    //returns list required by activity
    fun getWeatherList(): LiveData<List<WeatherResult>> {
        return weatherList
    }

    //get updated list from repo
    fun refreshWeatherList() {
        weatherList = weatherListRepository.getWeatherDetail()
    }

    //status of api request
    fun getIsRefreshing(): LiveData<Boolean> {
        return weatherListRepository.isRefreshing
    }

    //sort list by city
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

    //sort list by temperature
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