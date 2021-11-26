package ru.mrlargha.larghabike.domain.charts

import ru.mrlargha.larghabike.extensions.toKmH
import ru.mrlargha.larghabike.model.domain.TelemetryFrame
import ru.mrlargha.larghabike.model.presentation.LineChartEntry
import javax.inject.Inject

/**
 * Создатель данных для графиков
 */
class ChartsDataCreator @Inject constructor() {

    companion object {
        private const val ENTRIES_BY_LINE_CHART = 75
    }

    /**
     * Создаёт данные для линейного графика
     *
     * @param frames список кадров телеметрии
     * @param dataExtractor функция для выбора нужных данных из [TelemetryFrame]
     * @return список точек для графика
     */
    fun createLineChartData(
        frames: List<TelemetryFrame>,
        dataExtractor: (TelemetryFrame) -> Float
    ): List<LineChartEntry> {
        val duration = (frames.last().time.time - frames.first().time.time) / 1000

        val precisionSeconds = duration / ENTRIES_BY_LINE_CHART

        if (precisionSeconds < 1) {
            return emptyList()
        }

        var currentFramePosition = 0

        val xData = mutableListOf<Float>()
        val yData = mutableListOf<Float>()

        var currentTime: Long = frames.first().time.time

        val accumulator = mutableListOf<Float>()

        for (i in 0..(duration / precisionSeconds)) {
            repeat(precisionSeconds.toInt()) {
                if (currentFramePosition != frames.size && frames[currentFramePosition].time.time < (currentTime + (1000 * precisionSeconds))) {
                    accumulator.add(dataExtractor(frames[currentFramePosition]))
                    currentFramePosition++
                }
            }
            val average = accumulator.average().toFloat()
            if (average > 0f) {
                xData.add(((currentTime - frames.first().time.time) / 1000).toFloat())
                if (accumulator.isEmpty()) {
                    yData.add(0f)
                } else {
                    yData.add(average.toKmH())
                }
            }
            accumulator.clear()
            currentTime += 1000 * precisionSeconds
        }
        return xData.zip(yData) { x, y -> LineChartEntry(x, y) }
    }

    /**
     * Создаёт данные для отрисовки линни средней скорости
     *
     * @param avSpeed средняя скорость в м\с
     * @param startTime время начала поездки
     * @param endTime время конца поездки
     * @return список точек для построения линии средней скорости
     */
    fun createAvSpeedBaselineData(
        avSpeed: Float, startTime: Long, endTime: Long
    ): List<LineChartEntry> {
        return listOf(
            LineChartEntry(0f, avSpeed.toKmH()),
            LineChartEntry((endTime - startTime) / 1000f, avSpeed.toKmH())
        )
    }
}