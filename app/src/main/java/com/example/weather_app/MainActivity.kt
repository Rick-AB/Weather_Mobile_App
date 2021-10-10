package com.example.weather_app

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_app.adapter.RecyclerViewAdapter
import com.example.weather_app.databinding.ActivityMainBinding
import com.example.weather_app.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity(), android.widget.SearchView.OnQueryTextListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private var adapter: RecyclerViewAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeData()
        initRecyclerView()
        setListeners()

    }

    private fun setListeners() {
        binding.swipeRefresh.setOnRefreshListener { viewModel.refreshWeatherList() }
        binding.searchView.setOnQueryTextListener(this)
    }

    private fun observeData() {
        viewModel = ViewModelProvider.NewInstanceFactory().create(MainActivityViewModel::class.java)
        viewModel.init()
        viewModel.getWeatherList().observe(this) {
            Log.d("TAG", "observeData: $it")
            if (it != null && it.isNotEmpty()) {
                showViews()
                adapter?.updateList(it)
            } else if (it == null) {
                showError()
            }

        }
        viewModel.getIsRefreshing().observe(this) {
            binding.swipeRefresh.isRefreshing = it
        }
    }

    private fun showViews() {
        binding.searchView.visibility = View.VISIBLE
        binding.weatherRv.visibility = View.VISIBLE
        binding.lastUpdatedTv.visibility = View.VISIBLE

        binding.errorTv.visibility = View.GONE

        updateViewData()
    }

    private fun updateViewData() {
        val lastUpdated = "Last update: ${viewModel.getWeatherList().value!![0].lastUpdated}"
        Log.d("TAG", "updateViewData: $lastUpdated")
        binding.lastUpdatedTv.text = lastUpdated
    }

    private fun showError() {
        binding.searchView.visibility = View.GONE
        binding.weatherRv.visibility = View.GONE
        binding.lastUpdatedTv.visibility = View.GONE

        binding.errorTv.visibility = View.VISIBLE
    }

    private fun initRecyclerView() {
        binding.weatherRv.isNestedScrollingEnabled = false

        adapter = RecyclerViewAdapter(this, viewModel.getWeatherList().value!!)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.weatherRv.adapter = adapter
        binding.weatherRv.layoutManager = layoutManager
        binding.weatherRv.setHasFixedSize(true)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        adapter?.filter?.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter?.filter?.filter(newText)
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.by_city_asc -> viewModel.sortByCity("asc")
            R.id.by_city_dec -> viewModel.sortByCity("dec")
            R.id.by_temp_asc -> viewModel.sortByTemp("asc")
            R.id.by_temp_dec -> viewModel.sortByTemp("dec")

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.searchView.isInEditMode) {
            Log.d("TAG", "onBackPressed: ISFL")
            binding.rootView.requestFocus()
            binding.searchView.clearFocus()
        } else {
            super.onBackPressed()
        }

    }

    override fun onPause() {
        super.onPause()
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
    }
}