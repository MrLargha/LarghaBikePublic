package ru.mrlargha.larghabike.presentation.ui.screens.bikeselect

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearSnapHelper
import dagger.hilt.android.AndroidEntryPoint
import ru.mrlargha.larghabike.databinding.ActivityBikeSelectBinding
import ru.mrlargha.larghabike.presentation.ui.screens.bikesetup.BikeSetupActivity
import ru.mrlargha.larghabike.presentation.ui.viewmodels.BikeSelectViewModel

@AndroidEntryPoint
class BikeSelectActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, BikeSelectActivity::class.java)
        }
    }

    private val viewModel: BikeSelectViewModel by viewModels()
    private lateinit var binding: ActivityBikeSelectBinding
    private lateinit var adapter: BikesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBikeSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = BikesAdapter(callback = { bike ->
            // переделать на observe livedati
            viewModel.selectBike(bike)
            adapter.setSelectedBikeId(bike.id)
        })

        LinearSnapHelper().attachToRecyclerView(binding.recyclerView)

        binding.addBikeButton.setOnClickListener {
            startActivity(BikeSetupActivity.newIntent(this))
        }

        binding.recyclerView.adapter = adapter

        subscribeUI()
    }

    private fun subscribeUI() {
        viewModel.bikesListLiveData.observe(this) {
            adapter.setList(it)
        }

        viewModel.selectedBikeLiveData.observe(this) {
            adapter.setSelectedBikeId(it.id)
        }
    }

}