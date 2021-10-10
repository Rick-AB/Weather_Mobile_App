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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.Temporal
import java.time.temporal.TemporalAccessor
import java.util.*

class DetailsActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var data: MutableLiveData<OneCallResponse>? = null
    private lateinit var weatherDetailsRepository: WeatherDetailsRepository
    private val dateTimeFormatter = SimpleDateFormat("EEE dd MMMM h:mm a z", Locale.US)
    private var lat: Double? = null
    private var lon: Double? = null

    fun init(lat: Double, lon: Double) {
        this.lat = lat
        this.lon = lon
        if (data != null) {
            return
        }
        weatherDetailsRepository = WeatherDetailsRepository.getInstance()
        getWeatherDetailsByCoords()
    }

    fun getData(): LiveData<OneCallResponse> {
        return data!!
    }

    fun getWeatherDetailsByCoords() {
        data = weatherDetailsRepository.getWeatherDetailsByCoords(lat!!, lon!!)
    }

    fun getDailyWeather(): LiveData<List<Daily>> {
        return weatherDetailsRepository.dailyWeather
    }

    fun getHourlyWeather(): LiveData<List<Hourly>> {
        return weatherDetailsRepository.hourlyWeather
    }

    fun isRefreshing(): LiveData<Boolean> {
        return weatherDetailsRepository.isRefreshing
    }

    fun formIconUrl(string: String): String {
        return "${Constants.WEATHER_ICON_URL}${string}@2x.png"
    }

    fun uvIndexIntensity(uvi: Double): String {
        return when {
            uvi in 0.0..2.0 -> {
                "Low";
            }
            uvi in 3.0..5.0 -> {
                "Moderate";
            }
            uvi in 6.0..7.0 -> {
                "High";
            }
            uvi >= 8 -> {
                "Very high";
            }
            else -> {
                "";
            }
        }
    }


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