package com.example.weather_app.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.weather_app.model.WeatherResult
import com.example.weather_app.repository.network.WeatherClient
import com.example.weather_app.repository.network.WeatherService
import com.example.weather_app.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder
import java.util.*
import kotlin.collections.ArrayList

class WeatherListRepository {
    private val apiKey = Constants.API_KEY
    private val cities = Constants.CITIES
    private var result = MutableLiveData<List<WeatherResult>>()
    var isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)

    companion object {
        private var instance: WeatherListRepository? = null
        private lateinit var api: WeatherService
        fun getInstance(): WeatherListRepository {
            if (instance == null) {
                instance = WeatherListRepository()
                api = WeatherClient.getInstance(WeatherService::class.java)
            }

            return instance!!
        }
    }


    fun getWeatherDetail(): MutableLiveData<List<WeatherResult>> {
        val formatter = DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("h:mm a z")
            .toFormatter(Locale.US)
            .withZone(ZoneId.systemDefault())
        val lastUpdated = LocalTime.now().format(formatter).split(Regex("\\s"))[0]

        val list = ArrayList<WeatherResult>()
        result.value = list

        isRefreshing.value = true
        CoroutineScope(Dispatchers.IO).launch {
            cities.forEachIndexed { index, city ->
                run {
                    val call = api.getWeatherList(city, "metric", apiKey)
                    val response = call.execute()

                    val weatherResult = response.body()
                    if (response.isSuccessful && weatherResult != null) {
                        weatherResult.sys.img = Constants.CITY_IMAGES[index]
                        weatherResult.lastUpdated = lastUpdated
                        list.add(weatherResult)
                        if (index == cities.size - 1) {
                            isRefreshing.postValue(false)
                            result.postValue(list)
                        }
                    }

                    if (!response.isSuccessful) {
                        Log.d("TAG", "getDets: SHIIT")
                        if (index == cities.size - 1) {
                            isRefreshing.postValue(false)
                        }
                    }
                }
            }
        }
        return result
    }
}