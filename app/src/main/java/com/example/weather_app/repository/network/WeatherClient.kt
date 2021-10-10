package com.example.weather_app.repository.network

import com.example.weather_app.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherClient {
    companion object {
        private var mRetrofit: Retrofit? = null

        fun<T> getInstance(service: Class<T>): T {
            if (mRetrofit == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                mRetrofit = retrofit
            }
            return mRetrofit!!.create(service)
        }
    }
}