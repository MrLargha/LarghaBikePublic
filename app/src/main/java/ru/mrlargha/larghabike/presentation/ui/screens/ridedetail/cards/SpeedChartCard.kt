package ru.mrlargha.larghabike.presentation.ui.screens.ridedetail.cards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import ru.mrlargha.larghabike.R
import ru.mrlargha.larghabike.databinding.LineChartCardBinding
import ru.mrlargha.larghabike.model.presentation.ridestat.AbstractRideDetailCardData
import ru.mrlargha.larghabike.model.presentation.ridestat.SpeedChartCardData


class SpeedChartCard(itemView: View) : BaseViewHolder(itemView) {
    private var chart: LineChart = itemView.findViewById(R.id.chart)

    init {
        chart.axisLeft.apply {
            setDrawAxisLine(true)
            textColor = itemView.context.getColor(R.color.material_on_background_emphasis_medium)
            setDrawGridLines(false)
        }
        chart.axisRight.apply {
            isEnabled = false
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }
        chart.xAxis.apply {
            setDrawAxisLine(true)
            textColor = itemView.context.getColor(R.color.material_on_background_emphasis_medium)
            setDrawGridLines(false)
            setLabelCount(5, false)
            valueFormatter =
                object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${(value / 60).toInt()}:${(value % 60).toInt()}"
                    }
                }
        }
        chart.legend.textColor = itemView.context.getColor(R.color.material_on_background_emphasis_medium)
        chart.legend.isEnabled = true
        chart.isClickable = false
        chart.description = null
        chart.isEnabled = false
    }

    /**
     * TODO
     *
     * @param data
     */
    override fun bind(data: AbstractRideDetailCardData) {
        val binding = LineChartCardBinding.bind(itemView)
        binding.titleText.text = "Speed"
        binding.subtitleText.text = "Speed during your ride"

        val chartData = data as? SpeedChartCardData
            ?: throw IllegalArgumentException("Parameter data should be SpeedChartCardData")
        val entries = chartData.mainChartData.map { Entry(it.x, it.y) }
        val lineData = LineData(LineDataSet(entries, "Speed").apply {
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            lineWidth = 2f
            color = itemView.context.getColor(R.color.primaryColor)
            setDrawFilled(true)
            fillColor = itemView.context.getColor(R.color.primaryLightColor)
        })

        val avDataSet = LineDataSet(chartData.avSpeedBaselineData.map { Entry(it.x, it.y) }, "Average speed").apply {
            setDrawValues(false)
            setDrawCircles(false)
            lineWidth = 3f
            color = itemView.context.getColor(R.color.secondaryDarkColor)
        }
        lineData.addDataSet(avDataSet)

        chart.xAxis.axisMaximum = entries.maxOfOrNull { it.x } ?: 0f
        chart.xAxis.axisMinimum = entries.minOfOrNull { it.x } ?: 0f
        chart.axisLeft.axisMaximum = entries.maxOfOrNull { it.y }?.times(1.1f) ?: 0f
        chart.axisLeft.axisMinimum = 0f
        chart.data = lineData
        chart.invalidate()
    }

    companion object {
        /**
         * TODO
         *
         * @param parent
         * @return
         */
        fun create(parent: ViewGroup): BaseViewHolder {
            return SpeedChartCard(
                LineChartCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).root
            )
        }
    }
}