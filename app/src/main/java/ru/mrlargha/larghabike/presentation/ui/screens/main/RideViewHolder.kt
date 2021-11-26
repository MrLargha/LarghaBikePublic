package ru.mrlargha.larghabike.presentation.ui.screens.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mrlargha.larghabike.R
import ru.mrlargha.larghabike.data.util.Formatters
import ru.mrlargha.larghabike.databinding.RideViewBinding
import ru.mrlargha.larghabike.model.domain.Ride

class RideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: Ride, rideSelectedCallback: OnRideClickedCallback) {
        itemView.setOnClickListener {
            rideSelectedCallback.rideClicked(item)
        }
        RideViewBinding.bind(itemView).apply {
            rideDuration.text =
                "${Formatters.formatTimeHoursMinutes(item.endTime.time - item.startTime.time)}"
            rideDistance.text = Formatters.defaultNumberFormat.format(item.distance / 1000f)
            rideTime.text = Formatters.defaultDateTimeFormat.format(item.startTime)
        }
    }


    companion object {
        fun from(parent: ViewGroup): RideViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.ride_view, parent, false)
            return RideViewHolder(view)
        }
    }
}