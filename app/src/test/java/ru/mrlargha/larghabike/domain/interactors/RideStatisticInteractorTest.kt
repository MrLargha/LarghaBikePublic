package ru.mrlargha.larghabike.domain.interactors

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import ru.mrlargha.larghabike.data.util.Formatters
import ru.mrlargha.larghabike.domain.charts.ChartsDataCreator
import ru.mrlargha.larghabike.model.domain.Ride
import ru.mrlargha.larghabike.model.domain.RideWithFrames
import ru.mrlargha.larghabike.model.domain.TelemetryFrame
import ru.mrlargha.larghabike.model.presentation.LineChartEntry
import ru.mrlargha.larghabike.model.presentation.ridestat.OneValueCardData
import ru.mrlargha.larghabike.model.presentation.ridestat.SpeedChartCardData
import ru.mrlargha.larghabike.domain.repos.IMapPreviewRepository
import ru.mrlargha.larghabike.domain.repos.IRideRepository
import ru.mrlargha.larghabike.extensions.toKmH
import ru.mrlargha.larghabike.getOrAwaitValue
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class RideStatisticInteractorTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var bitmap: Bitmap

    @Mock
    lateinit var rideRepository: IRideRepository

    @Mock
    lateinit var mapPreviewRepository: IMapPreviewRepository

    @Mock
    lateinit var chartsDataCreator: ChartsDataCreator

    private lateinit var interactor: RideStatisticInteractor

    @Before
    fun setUp() {
        interactor =
            RideStatisticInteractor(rideRepository, mapPreviewRepository, chartsDataCreator)
    }

    @Test
    fun getFinishedRides() {
        val liveData = MutableLiveData(rides)
        whenever(rideRepository.getAllFinishedRides()).thenReturn(liveData)

        val result = interactor.getFinishedRides()

        assertThat(result.getOrAwaitValue()).isEqualTo(rides)
        verify(rideRepository, times(1)).getAllFinishedRides()
        verifyNoMoreInteractions(rideRepository)
        verifyNoInteractions(mapPreviewRepository)
    }

    @Test
    fun getMapPreviewForRide() {
        whenever(rideRepository.getRideWithFrames(any())).thenReturn(Single.just(rideWithFrames))
        whenever(mapPreviewRepository.getMapForRide(any(), any())).thenReturn(Single.just(bitmap))

        val result = interactor.getMapPreviewForRide(sampleRide.id)

        assertThat(result.blockingGet()).isEqualTo(bitmap)
        verify(rideRepository, times(1)).getRideWithFrames(sampleRide.id)
        verify(mapPreviewRepository, times(1)).getMapForRide(sampleRide, frames)
        verifyNoMoreInteractions(mapPreviewRepository, rideRepository)
    }

    @Test
    fun getStatisticCardsListForRide() {
        whenever(chartsDataCreator.createLineChartData(any(), any())).thenReturn(lineChartEntries)
        whenever(chartsDataCreator.createAvSpeedBaselineData(any(), any(), any())).thenReturn(
            lineChartEntries
        )
        whenever(rideRepository.getRideWithFrames(any())).thenReturn(Single.just(rideWithFrames))

        val result = interactor.getStatisticCardsListForRide(1).blockingGet()
        val speedCharts = result.filterIsInstance<SpeedChartCardData>()
        assertThat(speedCharts).isNotEmpty()
        assertThat(speedCharts.size).isEqualTo(1)

        val chartData = speedCharts.first()
        assertThat(chartData.mainChartData).isEqualTo(lineChartEntries)
        assertThat(chartData.avSpeedBaselineData).isEqualTo(lineChartEntries)

        val oneValueCards = result.filterIsInstance<OneValueCardData>()
        assertThat(oneValueCards.find { it.title.equals("Average Speed", true) }?.value).isEqualTo(
            Formatters.defaultNumberFormat.format(sampleRide.avSpeed.toKmH())
        )
        assertThat(oneValueCards.size).isEqualTo(6)

        verify(chartsDataCreator, times(1)).createLineChartData(any(), any())
        verify(chartsDataCreator, times(1)).createAvSpeedBaselineData(any(), any(), any())
        verify(rideRepository, times(1)).getRideWithFrames(any())
        verifyNoMoreInteractions(chartsDataCreator, rideRepository)
    }

    companion object {
        val sampleRide =
            Ride(
                1,
                Date().also { it.time -= 60 * 60 * 1000 },
                Date(),
                0,
                50f,
                25f,
                25f,
                25000f,
                true,
                1
            )
        val rides = listOf(sampleRide, sampleRide, sampleRide)
        val frames = generateFrames()
        val rideWithFrames = RideWithFrames(sampleRide, frames)

        val lineChartEntries =
            listOf(LineChartEntry(1f, 1f), LineChartEntry(1f, 1f), LineChartEntry(1f, 1f))

        private fun generateFrames(): List<TelemetryFrame> {
            val resultList = mutableListOf<TelemetryFrame>()
            repeat(10) {
                resultList.add(
                    TelemetryFrame(
                        it,
                        1,
                        Date(),
                        10f,
                        10f,
                        10,
                        0
                    )
                )
            }
            return resultList
        }
    }
}