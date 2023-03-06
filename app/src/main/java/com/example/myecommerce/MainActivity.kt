package com.example.myecommerce

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myecommerce.databinding.ActivityMainBinding
import com.example.myecommerce.ui.trolley.TrolleyActivity
import com.example.myecommerce.ui.trolley.TrolleyViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<TrolleyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        binding.icCart.setOnClickListener {
            startActivity(Intent(this@MainActivity, TrolleyActivity::class.java))
        }

        setupBadgeTrolley()
    }

    private fun setupBadgeTrolley() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getAllProduct().collect { result ->
                    if (result.isNotEmpty()) {
                        binding.imgBadges.visibility = View.VISIBLE
                        binding.tvBadgesMenu.visibility = View.VISIBLE
                        binding.tvBadgesMenu.text = result.size.toString()
                    } else {
                        binding.imgBadges.visibility = View.GONE
                        binding.tvBadgesMenu.visibility = View.GONE
                    }
                }
            }
        }
    }
}