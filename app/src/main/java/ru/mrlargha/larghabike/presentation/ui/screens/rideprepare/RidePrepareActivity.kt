package ru.mrlargha.larghabike.presentation.ui.screens.rideprepare

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import ru.mrlargha.larghabike.databinding.ActivityRidePrepareBinding
import ru.mrlargha.larghabike.presentation.ui.screens.ridehost.RideHostActivity
import ru.mrlargha.larghabike.presentation.ui.viewmodels.RidePrepareViewModel

@AndroidEntryPoint
class RidePrepareActivity : AppCompatActivity() {

    companion object {
        const val TAG = "RidePrepareActivity"

        fun newIntent(context: Context): Intent {
            return Intent(context, RidePrepareActivity::class.java)
        }
    }

    private lateinit var binding: ActivityRidePrepareBinding

    private val viewModel: RidePrepareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRidePrepareBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeUI()
    }

    private fun subscribeUI() {
        viewModel.countDownLiveData.observe(this) {
            binding.countdown.text = it.toString()
            if (it == 0) {
                startActivity(RideHostActivity.newIntent(this))
                finish()
            }
        }
    }
}