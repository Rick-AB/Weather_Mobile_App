package com.example.weather_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather_app.databinding.HourlyWeatherListItemBinding
import com.example.weather_app.model.Daily
import com.example.weather_app.model.Hourly
import com.example.weather_app.model.WeatherResult
import com.example.weather_app.util.Constants
import java.text.SimpleDateFormat
import java.util.*

class HourlyWeatherAdapter(private val context: Context, private var list: List<Hourly>) :
    RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder>() {
    private val formatter = SimpleDateFormat("h a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        val binding = HourlyWeatherListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return HourlyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                val netDate = dt.toLong() * 1000
                val hour = formatter.format(netDate)
                val temp = "${temp.toInt()}°"
                val iconUrl = formIconUrl(weather[0].icon)
                binding.hourlyWeatherTimeTv.text = hour
                binding.hourlyWeatherTempTv.text = temp

                Glide.with(context)
                    .load(iconUrl)
                    .centerCrop()
                    .into(binding.hourlyWeatherImg)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: List<Hourly>) {
        this.list = list
        notifyDataSetChanged()
    }

    private fun formIconUrl(string: String): String = "${Constants.WEATHER_ICON_URL}${string}@2x.png"

    class HourlyWeatherViewHolder(val binding: HourlyWeatherListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}