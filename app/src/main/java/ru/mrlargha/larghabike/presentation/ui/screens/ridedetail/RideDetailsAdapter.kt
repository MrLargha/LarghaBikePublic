package ru.mrlargha.larghabike.presentation.ui.screens.ridedetail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mrlargha.larghabike.model.presentation.ridestat.AbstractRideDetailCardData
import ru.mrlargha.larghabike.model.presentation.ridestat.OneValueCardData
import ru.mrlargha.larghabike.model.presentation.ridestat.SpeedChartCardData
import ru.mrlargha.larghabike.presentation.ui.screens.ridedetail.cards.BaseViewHolder
import ru.mrlargha.larghabike.presentation.ui.screens.ridedetail.cards.OneValueCard
import ru.mrlargha.larghabike.presentation.ui.screens.ridedetail.cards.SpeedChartCard

class RideDetailsAdapter(
    private val elements: List<AbstractRideDetailCardData>
) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (ViewType.values()[viewType]) {
            ViewType.SINGLE_VALUE -> OneValueCard.create(parent)
            ViewType.SPEED_CHART -> SpeedChartCard.create(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(elements[position])
    }

    override fun getItemCount(): Int {
        return elements.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (elements[position]) {
            is OneValueCardData -> ViewType.SINGLE_VALUE.ordinal
            is SpeedChartCardData -> ViewType.SPEED_CHART.ordinal
            else -> throw IllegalArgumentException("Incorrect element of type: ${position.javaClass}")
        }
    }

    enum class ViewType {
        SINGLE_VALUE, SPEED_CHART
    }
}