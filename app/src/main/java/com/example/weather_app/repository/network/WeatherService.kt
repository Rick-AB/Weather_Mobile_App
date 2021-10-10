package com.example.weather_app.repository.network

import com.example.weather_app.model.OneCallResponse
import com.example.weather_app.model.WeatherResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    fun getWeatherList(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Call<WeatherResult>

    @GET("onecall")
    fun getWeatherDetailsByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Call<OneCallResponse>
}