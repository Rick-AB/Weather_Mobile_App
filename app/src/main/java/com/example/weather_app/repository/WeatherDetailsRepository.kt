package com.example.weather_app.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.weather_app.model.Daily
import com.example.weather_app.model.Hourly
import com.example.weather_app.model.OneCallResponse
import com.example.weather_app.repository.network.WeatherClient
import com.example.weather_app.repository.network.WeatherService
import com.example.weather_app.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherDetailsRepository {
    private val apiKey = Constants.API_KEY
    private val data = MutableLiveData<OneCallResponse>()
    val dailyWeather = MutableLiveData<List<Daily>>()
    val hourlyWeather = MutableLiveData<List<Hourly>>()
    val isRefreshing = MutableLiveData(false)

    companion object {
        private var instance: WeatherDetailsRepository? = null
        private lateinit var api: WeatherService
        fun getInstance(): WeatherDetailsRepository {
            if (instance == null) {
                instance = WeatherDetailsRepository()
                api = WeatherClient.getInstance(WeatherService::class.java)
            }

            return instance!!
        }
    }

    fun getWeatherDetailsByCoords(lat: Double, lon: Double): MutableLiveData<OneCallResponse> {
        var dailyList = ArrayList<Daily>()
        var hourlyList = ArrayList<Hourly>()
        dailyWeather.value = dailyList
        hourlyWeather.value = hourlyList

        isRefreshing.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val call =
                api.getWeatherDetailsByCoords(lat, lon, "alerts,minutely", "metric", apiKey)
            val response = call.execute()

            val oneCallResponse = response.body()
            if (response.isSuccessful) {
                isRefreshing.postValue(false)
                data.postValue(oneCallResponse!!)

                dailyList = ArrayList(oneCallResponse.daily)
                hourlyList = ArrayList(oneCallResponse.hourly)
                dailyWeather.postValue(dailyList)
                hourlyWeather.postValue(hourlyList)
            }

            if (!response.isSuccessful) {
                isRefreshing.postValue(false)
                Log.d(
                    "TAG",
                    "getWeatherDetailsByCoords: SOMETHING HAPPENED ${response.message()}"
                )
            }
        }
        return data
    }
}