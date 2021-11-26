package ru.mrlargha.larghabike.presentation.ui.screens.ridehost.fragments

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.mrlargha.larghabike.R
import ru.mrlargha.larghabike.data.util.Formatters
import ru.mrlargha.larghabike.databinding.FragmentSpeedometerBinding
import ru.mrlargha.larghabike.extensions.toKmH
import ru.mrlargha.larghabike.model.domain.GPSState
import ru.mrlargha.larghabike.presentation.services.TrackerService
import ru.mrlargha.larghabike.presentation.ui.screens.ridedetail.RideDetailActivity
import ru.mrlargha.larghabike.presentation.ui.viewmodels.RideHostViewModel
import kotlin.math.roundToInt

@AndroidEntryPoint
class SpeedometerFragment : Fragment() {

    companion object {
        private const val TAG = "SpeedometerFragment"
    }

    private val viewModel: RideHostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSpeedometerBinding.inflate(inflater, container, false)

        subscribeUI(binding)

        binding.apply {
            stopButton.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext()).setTitle("Finish")
                    .setMessage("Are you sure want to finish this ride?")
                    .setPositiveButton("Finish") { _: DialogInterface, _: Int ->
                        stopTrackingService()
                    }
                    .setNegativeButton("cancel") { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }
                    .create().show()
            }
        }

        return binding.root
    }

    private fun subscribeUI(binding: FragmentSpeedometerBinding) {
        viewModel.rideId.observe(this) {
            Log.d(TAG, "subscribeUI: new rideId: $id")
            if (it > 0) {
                startActivity(RideDetailActivity.newIntent(requireContext(), it))
                activity?.finish()
            }
        }

        viewModel.trackerData.observe(viewLifecycleOwner) {
            Log.i(TAG, "subscribeUI: DATA $it")
            it.statisticFrame?.let {
                binding.apply {
                    speedometer.odometerText = "${it.totalDistance.roundToInt()} m"
                    avSpeed.text = Formatters.defaultNumberFormat.format(it.averageSpeed.toKmH())
                    maxSpeed.text = Formatters.defaultNumberFormat.format(it.maxSpeed.toKmH())
                    lastKilometerPace.text = formatTime(it.lastKilometerPace)
                    totalTime?.text = formatTime(it.totalTime)
                    movingTime?.text = formatTime(it.movingTime)
                    stopTime?.text = formatTime(it.totalTime - it.movingTime)
                }
            }
            it.telemetryFrame?.let { frame ->
                binding.speedometer.setSpeed(frame.currentSpeed.toKmH(), 1000)
                binding.rpm.text = frame.rotationsPerMinute.toString()
            }
        }

        viewModel.gpsStatus.observe(this) {
            binding.gpsStatusImage.setImageResource(
                when (it) {
                    GPSState.FIX -> R.drawable.ic_baseline_location_on_24
                    GPSState.AWAITING_DATA -> R.drawable.ic_baseline_not_listed_location_24
                    GPSState.NOT_AVAILABLE -> R.drawable.ic_baseline_wrong_location_24
                    GPSState.DISABLED -> R.drawable.ic_baseline_location_off_24
                    else -> R.drawable.ic_baseline_wrong_location_24
                }
            )

            binding.gpsStatusImage.imageTintList = ColorStateList.valueOf(
                when (it) {
                    GPSState.FIX -> Color.GREEN
                    GPSState.AWAITING_DATA -> Color.YELLOW
                    GPSState.NOT_AVAILABLE -> Color.RED
                    GPSState.DISABLED -> Color.GRAY
                    else -> Color.GRAY
                }
            )
        }

    }

    private fun stopTrackingService() {
        Log.d(TAG, "stopService: stopping service")
        requireContext().stopService(TrackerService.getStopServiceIntent(requireContext()))
    }

    private fun formatTime(time: Long): String {
        val minutes: Long = time / 1000 / 60
        val seconds: Long = (time - minutes * 60 * 1000) / 1000
        return "$minutes'$seconds\""
    }
}