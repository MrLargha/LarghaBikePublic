package ru.mrlargha.larghabike.presentation.ui.screens.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.mrlargha.larghabike.R
import ru.mrlargha.larghabike.databinding.ActivityMainBinding
import ru.mrlargha.larghabike.model.domain.Ride
import ru.mrlargha.larghabike.presentation.ui.screens.bikeselect.BikeSelectActivity
import ru.mrlargha.larghabike.presentation.ui.screens.ridedetail.RideDetailActivity
import ru.mrlargha.larghabike.presentation.ui.screens.rideprepare.RidePrepareActivity
import ru.mrlargha.larghabike.presentation.ui.screens.settings.SettingsActivity
import ru.mrlargha.larghabike.presentation.ui.viewmodels.MainViewModel


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var adapter: RidesAdapter
    private val viewModel: MainViewModel by viewModels()

    private val isQOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    private val isMarshmallowOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                tryLaunchTracking()
            } else {
                MaterialAlertDialogBuilder(this).setTitle("Разрешите доступ к местоположению")
                    .setMessage(
                        "Для работы приложению необходим доступ к местоположению, в том числе в фоновом режиме." +
                                " Подключение к Bluetooth-устройству также невозможно без доступа к местоположению устройства"
                    )
                    .setPositiveButton("Ok") { dialogInterface: DialogInterface, _: Int ->
                        tryLaunchTracking()
                        dialogInterface.dismiss()
                    }
                    .create()
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        adapter = RidesAdapter { startRideDetailActivity(it) }

        binding.apply {

            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = adapter

            floatingActionButton.setOnClickListener {
                tryLaunchTracking()
            }
            bottomAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_settings -> startActivity(
                        Intent(
                            this@MainActivity,
                            SettingsActivity::class.java
                        )
                    )
                    R.id.bikes -> startActivity(BikeSelectActivity.newIntent(this@MainActivity))
                }
                true
            }
        }

        viewModel.rides.observe(this) {
            Log.d(TAG, "New rides list: $it")
            adapter.submitList(it)
            binding.ridesPlaceholder.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        setContentView(binding.root)
    }


    private fun tryLaunchTracking() {
        if (isMarshmallowOrAbove) {
            if (!isLocationPermissionsGranted(this)) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                return
            }

            @SuppressLint("InlinedApi")
            // Version code was checked in Util
            if (!isBackgroundLocationPermissionsGranted(this)) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                return
            }
        }

        if (!checkGPSEnabled(this)) {
            MaterialAlertDialogBuilder(this).setTitle("Включите GPS")
                .setMessage("GPS необходим для записи траектории вашей поездки, опредения скорости, а также для подключения внешнего спидометра по Bluetooth LE.")
                .setPositiveButton("Включить GPS") { _: DialogInterface, _: Int ->
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("Отмена") { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                .create().show()
            return
        }

        launchTracking()
    }

    private fun launchTracking() {
        if (!viewModel.hasAvailableBike()) {
            MaterialAlertDialogBuilder(this).setTitle("Cannot start ride")
                .setMessage("You don't have any selected bike for ride! Please select or create a new one")
                .setPositiveButton("Select bike") { _: DialogInterface, _: Int ->
                    startActivity(BikeSelectActivity.newIntent(this))
                }
                .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                .create().show()
            return
        }

        startActivity(RidePrepareActivity.newIntent(this))
    }

    private fun startRideDetailActivity(ride: Ride) {
        startActivity(RideDetailActivity.newIntent(this, ride.id))
    }

    private fun isLocationPermissionsGranted(context: Context) =
        (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)

    private fun isBackgroundLocationPermissionsGranted(context: Context) =
        (!isQOrAbove || (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED))

    private fun checkGPSEnabled(context: Context) =
        (context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager)?.isProviderEnabled(
            LocationManager.GPS_PROVIDER
        ) ?: false

}