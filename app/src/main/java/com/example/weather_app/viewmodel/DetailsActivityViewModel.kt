package com.example.weather_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weather_app.model.Daily
import com.example.weather_app.model.Hourly
import com.example.weather_app.model.OneCallResponse
import com.example.weather_app.repository.WeatherDetailsRepository
import com.example.weather_app.util.Constants
import java.text.SimpleDateFormat
import java.util.*

class DetailsActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var data: MutableLiveData<OneCallResponse>? = null
    private lateinit var weatherDetailsRepository: WeatherDetailsRepository
    private var lat: Double? = null
    private var lon: Double? = null

    //create instance of repo and fetch initial data
    fun init(lat: Double, lon: Double) {
        this.lat = lat
        this.lon = lon
        if (data != null) {
            return
        }
        weatherDetailsRepository = WeatherDetailsRepository.getInstance()
        getWeatherDetailsByCoords()
    }

    //returns weather details of selected city
    fun getData(): LiveData<OneCallResponse> {
        return data!!
    }

    //gets weather details of selected city from repo
    fun getWeatherDetailsByCoords() {
        data = weatherDetailsRepository.getWeatherDetailsByCoords(lat!!, lon!!)
    }

    //returns list of daily forecast
    fun getDailyWeather(): LiveData<List<Daily>> {
        return weatherDetailsRepository.dailyWeather
    }

    //returns list of hourly forecast
    fun getHourlyWeather(): LiveData<List<Hourly>> {
        return weatherDetailsRepository.hourlyWeather
    }

    //status of api request
    fun isRefreshing(): LiveData<Boolean> {
        return weatherDetailsRepository.isRefreshing
    }

    fun formIconUrl(string: String): String {
        return "${Constants.WEATHER_ICON_URL}${string}@2x.png"
    }

    //convert uv index to meaningful strings
    fun uvIndexIntensity(uvi: Double): String {
        return when {
            uvi in 0.0..2.0 -> {
                "Low"
            }
            uvi in 3.0..5.0 -> {
                "Moderate"
            }
            uvi in 6.0..7.0 -> {
                "High"
            }
            uvi >= 8 -> {
                "Very high"
            }
            else -> {
                ""
            }
        }
    }

    //format timestamp to readable string
    fun formatDateTime(unix: Int, dateTime: Boolean): String {
        val formatter: SimpleDateFormat = if (dateTime) {
            SimpleDateFormat("EEE dd MMMM h:mm a", Locale.getDefault())
        } else {
            SimpleDateFormat("h:mm a", Locale.getDefault())
        }

        val netDate = Date(unix.toString().toLong() * 1000)
        return formatter.format(netDate)
    }


}