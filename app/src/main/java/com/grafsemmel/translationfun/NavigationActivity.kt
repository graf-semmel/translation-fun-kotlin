package com.grafsemmel.translationfun

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.grafsemmel.translationfun.utils.ConnectivityLiveData
import kotlinx.android.synthetic.main.activity_navigation.*
import org.koin.android.ext.android.inject

class NavigationActivity : AppCompatActivity() {
    private var connectionSnackbar: Snackbar? = null
    private val connectivityLiveData: ConnectivityLiveData by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        setupBottomBar()
        setupObservers()
    }

    private fun setupObservers() {
        connectivityLiveData.observe(this, Observer { isConnected ->
            if (!isConnected) {
                Snackbar.make(nav_view, getString(R.string.snack_no_internet), Snackbar.LENGTH_INDEFINITE)
                        .also { connectionSnackbar = it }
                        .show()
            } else {
                connectionSnackbar?.dismiss()
            }
        })
    }

    private fun setupBottomBar() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
                setOf(R.id.navigation_search, R.id.navigation_recent, R.id.navigation_popular)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
