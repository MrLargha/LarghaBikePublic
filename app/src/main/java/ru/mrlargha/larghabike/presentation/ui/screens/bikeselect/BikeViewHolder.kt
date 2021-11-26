package ru.mrlargha.larghabike.presentation.ui.screens.bikeselect

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.mrlargha.larghabike.R
import ru.mrlargha.larghabike.databinding.BikeLargeViewBinding
import ru.mrlargha.larghabike.model.domain.Bike

class BikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = BikeLargeViewBinding.bind(itemView)
    fun bind(bike: Bike, isSelected: Boolean, callback: BikeSelectedCallback) {

        binding.apply {
            bikeName.text = bike.name
            wheelDiameter.text = "${bike.wheelDiameter}\""

            if (bike.photoPath == null) {
                bikeImage.setImageResource(R.drawable.bike_picture)
            }

            bike.photoPath?.let {
                bikeImage.setImageURI(Uri.parse(it))
            }

            if (isSelected) {
                button.text = "SELECTED"
                button.isEnabled = false
            } else {
                button.text = "SELECT"
                button.isEnabled = true
                button.setOnClickListener {
                    callback.selected(bike)
                }
            }
        }
    }
}
