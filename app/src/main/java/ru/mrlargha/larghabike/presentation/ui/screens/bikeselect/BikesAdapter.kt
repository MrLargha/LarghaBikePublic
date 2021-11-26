package ru.mrlargha.larghabike.presentation.ui.screens.bikeselect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mrlargha.larghabike.databinding.BikeLargeViewBinding
import ru.mrlargha.larghabike.model.domain.Bike


class BikesAdapter(
    private var bikesList: List<Bike> = emptyList(),
    private val callback: BikeSelectedCallback,
    private var selectedBikeId: Long = -1
) : RecyclerView.Adapter<BikeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BikeViewHolder {
        return BikeViewHolder(
            BikeLargeViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    override fun onBindViewHolder(holder: BikeViewHolder, position: Int) {
        val bike = bikesList[position]
        holder.bind(bike, bike.id == selectedBikeId, callback)
    }

    override fun getItemCount() = bikesList.size

    // TODO Switch to list adapter

    fun setList(it: List<Bike>) {
        bikesList = it
        notifyDataSetChanged()
    }

    fun setSelectedBikeId(id: Long) {
        selectedBikeId = id
        notifyDataSetChanged()
    }

}
