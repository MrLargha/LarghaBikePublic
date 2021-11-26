package ru.mrlargha.larghabike.presentation.ui.screens.ridedetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.mrlargha.larghabike.databinding.ActivityRideDetailBinding
import ru.mrlargha.larghabike.presentation.ui.viewmodels.RideDetailViewModel


@AndroidEntryPoint
class RideDetailActivity : AppCompatActivity() {

    companion object {
        const val RIDE_ID_KEY = "RIDE_ID"
        const val TAG = "RideDetailActivity"

        fun newIntent(context: Context, rideId: Long): Intent {
            val intent = Intent(context, RideDetailActivity::class.java)
            intent.putExtra(RIDE_ID_KEY, rideId)
            return intent
        }
    }

    private lateinit var binding: ActivityRideDetailBinding

    private val viewModel: RideDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRideDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.collapsingToolbarLayout.title = "RideEntity details"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        viewModel.rideMapImageLiveData.observe(this) {
            binding.imageView.setImageBitmap(it)
        }

        viewModel.cardDataListLiveData.observe(this) { list ->
            Log.i(TAG, "New cards: $list")
            binding.recyclerView.adapter = RideDetailsAdapter(list)
            (binding.recyclerView.layoutManager as GridLayoutManager).spanSizeLookup =
                object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        val type =
                            RideDetailsAdapter.ViewType.values()[(binding.recyclerView.adapter as RideDetailsAdapter).getItemViewType(
                                position
                            )]
                        return when (type) {
                            RideDetailsAdapter.ViewType.SPEED_CHART -> 2
                            else -> 1
                        }
                    }
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


}