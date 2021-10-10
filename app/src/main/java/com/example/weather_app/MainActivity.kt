package com.example.weather_app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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

        viewModel = ViewModelProvider.NewInstanceFactory().create(MainActivityViewModel::class.java)
        observeData()
        initRecyclerView()
        setListeners()
    }


    private fun setListeners() {
        binding.swipeRefresh.setOnRefreshListener { viewModel.refreshWeatherList() }
        binding.searchView.setOnQueryTextListener(this)
    }

    //observe changes in data from data source
    private fun observeData() {
        viewModel.init()
        viewModel.getWeatherList().observe(this) {
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

    //show view when data is received from source
    private fun showViews() {
        binding.searchView.visibility = View.VISIBLE
        binding.weatherRv.visibility = View.VISIBLE
        binding.lastUpdatedTv.visibility = View.VISIBLE

        binding.errorTv.visibility = View.GONE

        updateViewData()
    }

    //update view when data from source changes
    private fun updateViewData() {
        val lastUpdated = "Last update: ${viewModel.getWeatherList().value!![0].lastUpdated}"
        binding.lastUpdatedTv.text = lastUpdated
    }

    //show error when retrieving data fails
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

    //filter list shown in recycler view
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

    private fun checkFocusRec(view: View): Boolean {
        if (view.isFocused) return true
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                if (checkFocusRec(view.getChildAt(i))) return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        if (checkFocusRec(binding.searchView)) {
            binding.rootView.requestFocus()
            binding.searchView.clearFocus()
        } else {
            super.onBackPressed()
        }

    }

    //remove focus from search view when leaving activity
    override fun onPause() {
        super.onPause()
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
    }
}