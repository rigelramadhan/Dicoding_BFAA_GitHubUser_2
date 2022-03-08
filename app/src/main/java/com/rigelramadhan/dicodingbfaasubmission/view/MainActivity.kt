package com.rigelramadhan.dicodingbfaasubmission.view

import android.app.SearchManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rigelramadhan.dicodingbfaasubmission.R
import com.rigelramadhan.dicodingbfaasubmission.adapter.UserAdapter
import com.rigelramadhan.dicodingbfaasubmission.databinding.ActivityMainBinding
import com.rigelramadhan.dicodingbfaasubmission.util.LoadingStatus
import com.rigelramadhan.dicodingbfaasubmission.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this,
            ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]

        mainViewModel.usersList.observe(this) {
            binding.rvUsers.apply {
                adapter = UserAdapter(it)
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }

        mainViewModel.isLoading.observe(this) {
            when (it) {
                LoadingStatus.LOADING -> binding.progressBar.visibility = View.VISIBLE
                else -> binding.progressBar.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_title)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                mainViewModel.queryUsers(query ?: "a")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                mainViewModel.queryUsers()
                return true
            }

        })

        return true
    }
}