package ru.mrlargha.larghabike.presentation.ui.screens.ridedetail.cards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.mrlargha.larghabike.databinding.OneValueCardViewBinding
import ru.mrlargha.larghabike.model.presentation.ridestat.AbstractRideDetailCardData
import ru.mrlargha.larghabike.model.presentation.ridestat.OneValueCardData

class OneValueCard(itemView: View) : BaseViewHolder(itemView) {
    override fun bind(data: AbstractRideDetailCardData) {
        val binding = OneValueCardViewBinding.bind(itemView)
        val cardData = data as? OneValueCardData
            ?: throw IllegalArgumentException("Parameter data should be OneValueCardData")

        binding.apply {
            titleText.text = cardData.title
            subtitleText.text = cardData.subtitle
            valueText.text = cardData.value
            unitText.text = cardData.unit
        }
    }

    companion object {
        fun create(parent: ViewGroup): BaseViewHolder {
            return OneValueCard(
                OneValueCardViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).root
            )
        }
    }
}