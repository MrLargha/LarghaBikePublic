package ru.mrlargha.larghabike.presentation.ui.screens.main

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.mrlargha.larghabike.model.domain.Ride

class RidesAdapter(private val rideSelectedCallback: OnRideClickedCallback) :
    ListAdapter<Ride, RideViewHolder>(RideDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        return RideViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        holder.bind(getItem(position), rideSelectedCallback)
    }

}

class RideDiffCallback : DiffUtil.ItemCallback<Ride>() {
    override fun areItemsTheSame(oldItem: Ride, newItem: Ride): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Ride, newItem: Ride): Boolean {
        return oldItem == newItem
    }
}