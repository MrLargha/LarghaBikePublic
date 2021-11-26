package ru.mrlargha.larghabike.presentation.ui.screens.ridehost.fragments

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.AndroidEntryPoint
import ru.mrlargha.larghabike.R
import ru.mrlargha.larghabike.databinding.FragmentRoadmapBinding
import ru.mrlargha.larghabike.extensions.toKmH
import ru.mrlargha.larghabike.extensions.toLatLng
import ru.mrlargha.larghabike.presentation.ui.viewmodels.RideHostViewModel
import java.text.DecimalFormat


@AndroidEntryPoint
class RoadMapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        const val TAG = "RoadMapFragment"
    }

    private lateinit var map: GoogleMap
    private val viewModel: RideHostViewModel by viewModels()
    private var lastLocation: LatLng? = null
    private var bikeMarker: Marker? = null
    private val decimalFormat = DecimalFormat("#.#")
    private var mapReady = false
    private var polyline: Polyline? = null
    private var requireFullPathUpdate = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRoadmapBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    private fun subscribeUI() {
        val binding = FragmentRoadmapBinding.bind(requireView())
        viewModel.trackerData.observe(this) { trackerData ->
            Log.d(TAG, "subscribeUI: new tracker data")
            trackerData.telemetryFrame?.let { telemetryFrame ->
                binding.speed.text =
                    "${decimalFormat.format(telemetryFrame.currentSpeed.toKmH())} km/h"
                telemetryFrame.getLatLng()?.let { location ->
                    if (location != lastLocation) {
                        var bearing = 0.0
                        lastLocation?.let {
                            bearing =
                                SphericalUtil.computeHeading(it, location)
                        }
                        telemetryFrame.getLocation()?.let {
                            moveMap(it, bearing)
                        }
                        lastLocation = location
                        if (bikeMarker == null) {
                            bikeMarker = map.addMarker(
                                MarkerOptions().icon(
                                    bitmapDescriptorFromVector(
                                        requireContext(),
                                        R.drawable.ic_navigation_arrow
                                    )
                                ).position(lastLocation!!)
                            )
                        }
                        bikeMarker?.position = lastLocation!!
                        if (polyline == null) {
                            polyline = trackerData.path?.let {
                                PolylineOptions().addAll(it)
                                    .color(
                                        getPathColor()
                                    )
                                    .width(12f)
                            }?.let {
                                map.addPolyline(
                                    it
                                )
                            }
                        } else {
                            val points = polyline?.points.apply { this?.clear() }
                            trackerData.path?.let { points?.addAll(it) }
                            polyline?.points = points!!
                        }
                    }
                }
            }
        }
    }

    private fun getPathColor() = resources.getColor(
        R.color.secondaryLightColor,
        activity?.theme
    )

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable?.setTint(Color.GREEN)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun addNewSegment(location: LatLng) {
        lastLocation?.let {
            if (polyline == null) {
                polyline = map.addPolyline(
                    PolylineOptions().add(it)
                        .add(location)
                        .color(getPathColor())
                        .width(12f)
                )
            } else {
                val points = polyline?.points
                points?.add(location)
                polyline?.points = points!!
            }
        }
    }

    private fun moveMap(location: Location, bearing: Double = .0) {
        map.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.builder().target(location.toLatLng()).bearing(bearing.toFloat())
                    .zoom(16f)
                    .build()
            )
        )
    }

    private fun setZoom(zoom: Float) {
        map.moveCamera(CameraUpdateFactory.zoomBy(zoom))
    }

    override fun onPause() {
        super.onPause()
        requireFullPathUpdate = true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        mapReady = true
        try {
            if (!googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.night_map
                    )
                )
            ) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        setZoom(16f)
        subscribeUI()
    }

}