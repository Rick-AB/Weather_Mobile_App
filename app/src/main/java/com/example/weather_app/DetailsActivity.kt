package com.example.weather_app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weather_app.adapter.DailyWeatherAdapter
import com.example.weather_app.adapter.HourlyWeatherAdapter
import com.example.weather_app.databinding.ActivityDetailsBinding
import com.example.weather_app.viewmodel.DetailsActivityViewModel

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var viewModel: DetailsActivityViewModel
    private lateinit var city: String
    private var dailyWeatherAdapter: DailyWeatherAdapter? = null
    private var hourlyWeatherAdapter: HourlyWeatherAdapter? = null
    private var lat = 0.0
    private var lon = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application)
            .create(DetailsActivityViewModel::class.java)

        //get values required for api request
        lat = intent.getDoubleExtra("lat", 0.0)
        lon = intent.getDoubleExtra("lon", 0.0)
        city = intent.getStringExtra("city").toString()

        val imageUrl = intent.getStringExtra("imageUrl")
        Glide.with(this)
            .load(imageUrl)
            .centerCrop()
            .into(binding.backdrop)

        observeData()
        setListener()
        initRecyclerViews()
    }

    private fun setListener() {
        binding.detailsSwipeRefresh.setOnRefreshListener { viewModel.getWeatherDetailsByCoords() }
    }

    //observe changes in data from data source
    private fun observeData() {
        viewModel.init(lat, lon)
        viewModel.getData().observe(this) {
            if (it != null) {
                showViews()
                updateView()
            } else {
                showError()
            }
        }
        viewModel.getDailyWeather().observe(this) {
            dailyWeatherAdapter?.updateList(it)
        }
        viewModel.getHourlyWeather().observe(this) {
            hourlyWeatherAdapter?.updateList(it)
        }
        viewModel.isRefreshing().observe(this) {
            binding.detailsSwipeRefresh.isRefreshing = it
        }
    }

    //show error when data retrieval fails
    private fun showError() {
        binding.cardViewDetails.visibility = View.GONE
        binding.cardViewDaily.visibility = View.GONE
        binding.cardViewExtra.visibility = View.GONE
        binding.poweredBy.visibility = View.GONE
        binding.source.visibility = View.GONE

        binding.detailsErrorTv.visibility = View.VISIBLE
    }

    //show view when data is received from source
    private fun showViews() {
        binding.cardViewDetails.visibility = View.VISIBLE
        binding.cardViewDaily.visibility = View.VISIBLE
        binding.cardViewExtra.visibility = View.VISIBLE
        binding.poweredBy.visibility = View.VISIBLE
        binding.source.visibility = View.VISIBLE

        binding.detailsErrorTv.visibility = View.GONE
    }

    private fun initRecyclerViews() {
        dailyWeatherAdapter = DailyWeatherAdapter(this, viewModel.getDailyWeather().value!!)
        val dailyWeatherLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.dailyWeatherRv.adapter = dailyWeatherAdapter
        binding.dailyWeatherRv.layoutManager = dailyWeatherLayoutManager

        hourlyWeatherAdapter = HourlyWeatherAdapter(this, viewModel.getHourlyWeather().value!!)
        val hourlyWeatherLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.hourlyWeatherRv.adapter = hourlyWeatherAdapter
        binding.hourlyWeatherRv.layoutManager = hourlyWeatherLayoutManager
    }

    //update views when data changes
    private fun updateView() {
        //details view
        val maxMin =
            "${viewModel.getData().value!!.daily[0].temp.max.toInt()}°/${viewModel.getData().value!!.daily[0].temp.min.toInt()}°"
        val feelsLike = "Feels like ${viewModel.getData().value?.current?.feels_like?.toInt()}°"
        val imageUrl = viewModel.formIconUrl(viewModel.getData().value?.current!!.weather[0].icon)
        val currentTemp = "${viewModel.getData().value?.current?.temp?.toInt()}°"
        binding.weatherLocationTv.text = city
        binding.currentDateTv.text =
            viewModel.formatDateTime(viewModel.getData().value?.current?.dt!!, true)
        binding.currentTempTv.text = currentTemp
        binding.weatherDescTv.text = viewModel.getData().value?.current!!.weather[0].description.replaceFirstChar { it.titlecase() }
        binding.maxMinTempTv.text = maxMin
        binding.feelsLikeTv.text = feelsLike

        Glide.with(this)
            .load(imageUrl)
            .centerCrop()
            .into(binding.detailsWeatherIconImg)


        //extras
        val windSpeed =
            "${viewModel.getData().value?.current?.wind_speed?.toInt()?.toString()} km/h"
        val humidity = "${viewModel.getData().value?.current?.humidity}%"
        binding.uvIndexValue.text =
            viewModel.uvIndexIntensity(viewModel.getData().value?.current?.uvi!!)
        binding.sunriseValue.text =
            viewModel.formatDateTime(viewModel.getData().value?.current?.sunrise!!, false)
        binding.sunsetValue.text =
            viewModel.formatDateTime(viewModel.getData().value?.current?.sunset!!, false)
        binding.windValue.text = windSpeed
        binding.humidityValue.text = humidity
    }
}