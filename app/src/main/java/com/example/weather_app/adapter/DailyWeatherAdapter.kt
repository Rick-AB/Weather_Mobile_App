package com.example.weather_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather_app.databinding.DailyWeatherListItemBinding
import com.example.weather_app.model.Daily
import com.example.weather_app.util.Constants
import java.text.SimpleDateFormat
import java.util.*

class DailyWeatherAdapter(private val context: Context, private var list: List<Daily>) :
    RecyclerView.Adapter<DailyWeatherAdapter.DailyWeatherViewHolder>() {
    private val formatter = SimpleDateFormat("EEEE", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val binding =
            DailyWeatherListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return DailyWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                val netDate = dt.toLong() * 1000
                val day = formatter.format(netDate)
                val humidity = "$humidity%"
                val maxMin = "${temp.max.toInt()}°/${temp.min.toInt()}°"
                val maxTempIconUrl = formIconUrl(weather[0].icon)

                binding.dailyWeatherDayTv.text = day
                binding.dailyWeatherHumidityTv.text = humidity
                binding.dailyWeatherMaxMinTv.text = maxMin

                Glide.with(context)
                    .load(maxTempIconUrl)
                    .centerCrop()
                    .into(binding.dailyWeatherMaxImg)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: List<Daily>) {
        this.list = list
        notifyDataSetChanged()
    }

    private fun formIconUrl(string: String): String = "${Constants.WEATHER_ICON_URL}${string}@2x.png"

    class DailyWeatherViewHolder(val binding: DailyWeatherListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }


}