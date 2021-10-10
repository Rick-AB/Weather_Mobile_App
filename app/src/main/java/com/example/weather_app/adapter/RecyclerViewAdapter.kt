package com.example.weather_app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather_app.DetailsActivity
import com.example.weather_app.databinding.WeatherListItemBinding
import com.example.weather_app.model.WeatherResult
import com.example.weather_app.util.Constants

class RecyclerViewAdapter(private val context: Context, list: List<WeatherResult>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(), Filterable {

    private var filteredList = list
    private var fullList = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = WeatherListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(filteredList[position]) {
                val temp = "${main.temp.toInt()}°"
                binding.weatherCityTv.text = name
                binding.weatherCountryTv.text = sys.country
                binding.weatherTempTv.text = temp
                binding.weatherItemDescTv.text = weather[0].description.replaceFirstChar { it.titlecase() }

                val iconUrl = formIconUrl(weather[0].icon)
                Glide.with(context)
                    .load(iconUrl)
                    .centerCrop()
                    .into(binding.weatherIconImg)

                Glide.with(context)
                    .load(sys.img)
                    .centerCrop()
                    .into(binding.weatherLocationImg)

                binding.weatherItemContainer.setOnClickListener {
                    val intent = Intent(context, DetailsActivity::class.java)
                    intent.putExtra("lat", coord.lat)
                    intent.putExtra("lon", coord.lon)
                    intent.putExtra("city", name)
                    intent.putExtra("imageUrl", sys.img)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun updateList(list: List<WeatherResult>) {
        this.fullList = list
        this.filteredList = list
        notifyDataSetChanged()
    }

    private fun formIconUrl(string: String): String = "${Constants.WEATHER_ICON_URL}${string}@2x.png"

    class ViewHolder(val binding: WeatherListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    //filter list based on text query
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString() ?: ""
                filteredList = if (query.isEmpty()) fullList else {
                    filteredList = fullList
                    val filtered = ArrayList<WeatherResult>()
                    filteredList.filter { it.name.lowercase().contains(query) }.forEach { filtered.add(it) }
                    filtered
                }

                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = if (results?.values == null) ArrayList() else {
                    results.values as ArrayList<WeatherResult>
                }
                notifyDataSetChanged()
            }

        }
    }
}