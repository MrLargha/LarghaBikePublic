package ru.mrlargha.larghabike.presentation.ui.screens.ridehost

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.mrlargha.larghabike.databinding.ActivityRideHostBinding
import ru.mrlargha.larghabike.presentation.services.TrackerService
import ru.mrlargha.larghabike.presentation.ui.transformers.ZoomOutPageTransformer
import ru.mrlargha.larghabike.presentation.ui.viewmodels.RideHostViewModel

@AndroidEntryPoint
class RideHostActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "RideHostActivity"

        fun newIntent(context: Context): Intent {
            return Intent(context, RideHostActivity::class.java)
        }
    }

    private val viewModel: RideHostViewModel by viewModels()
    private lateinit var pagerAdapter: RideHostPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRideHostBinding.inflate(layoutInflater)
        val conf: Configuration = resources.configuration


        if (conf.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Не используем адаптеры в данной конфигурации
        } else {
            pagerAdapter = RideHostPagerAdapter(this)
            binding.apply {
                viewpager?.adapter = pagerAdapter
                TabLayoutMediator(tabLayout!!, viewpager!!) { tab, position ->
                    tab.text = when (position) {
                        0 -> "Telemetry"
                        else -> "Map"
                    }
                }.attach()
                viewpager.setPageTransformer(ZoomOutPageTransformer())
                viewpager.isUserInputEnabled = false
            }
        }

        setContentView(binding.root)
        supportActionBar?.hide()

        startTrackerService()
    }

    private fun startTrackerService() {
        Log.d(TAG, "Starting tracker service!")
        Intent(this, TrackerService::class.java).also {
            startService(it)
        }
    }
}
