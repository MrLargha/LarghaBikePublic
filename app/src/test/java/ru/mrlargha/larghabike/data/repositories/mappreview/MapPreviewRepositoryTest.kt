package ru.mrlargha.larghabike.data.repositories.mappreview

import android.graphics.Bitmap
import com.google.common.truth.Truth.assertThat
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import ru.mrlargha.larghabike.model.domain.Ride
import ru.mrlargha.larghabike.model.domain.TelemetryFrame
import java.util.*

@RunWith(MockitoJUnitRunner::class)
open class MapPreviewRepositoryTest {

    @Mock
    lateinit var networkProvider: NetworkMapPreviewProvider

    @Mock
    lateinit var bitmap: Bitmap

    @Mock
    lateinit var localProvider: LocalStorageMapPreviewProvider

    private lateinit var repository: MapPreviewRepository

    @Before
    fun setUp() {
        repository = MapPreviewRepository(networkProvider, localProvider)
    }

    @Test
    fun getMapForRideWithCachedMap() {
        whenever(localProvider.getMapPreview(sampleRide)).thenReturn(Single.just(bitmap))
        whenever(localProvider.hasPreviewForRide(sampleRide.id)).thenReturn(true)

        val result = repository.getMapForRide(sampleRide, frames)

        assertThat(result.blockingGet()).isEqualTo(bitmap)
        verify(localProvider, times(1)).hasPreviewForRide(sampleRide.id)
        verify(localProvider, times(1)).getMapPreview(sampleRide)
        verifyNoMoreInteractions(localProvider)
        verifyNoInteractions(networkProvider)
    }

    @Test
    fun getMapForRideWithoutCachedMap() {
        whenever(localProvider.hasPreviewForRide(sampleRide.id)).thenReturn(false)
        whenever(networkProvider.getPreview(any(), any(), any())).thenReturn(Single.just(bitmap))
        whenever(localProvider.saveMap(sampleRide.id, bitmap)).thenReturn(Completable.complete())

        val result = repository.getMapForRide(sampleRide, frames)

        assertThat(result.blockingGet()).isEqualTo(bitmap)
        verify(localProvider, times(1)).hasPreviewForRide(sampleRide.id)
        verify(networkProvider, times(1)).getPreview(any(), any(), any())
        verify(localProvider, times(1)).saveMap(sampleRide.id, bitmap)
        verifyNoMoreInteractions(localProvider)
        verifyNoMoreInteractions(networkProvider)
    }


    companion object {
        val frames = generateFrames()

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