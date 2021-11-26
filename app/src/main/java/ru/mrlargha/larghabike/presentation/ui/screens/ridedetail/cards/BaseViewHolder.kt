package ru.mrlargha.larghabike.presentation.ui.screens.ridedetail.cards

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.mrlargha.larghabike.model.presentation.ridestat.AbstractRideDetailCardData

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(data: AbstractRideDetailCardData)
}