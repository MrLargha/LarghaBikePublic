package ru.mrlargha.larghabike.data.repositories.ride

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import com.google.common.truth.Truth.assertThat
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import ru.mrlargha.larghabike.data.database.RideDAO
import ru.mrlargha.larghabike.model.data.RideEntity
import ru.mrlargha.larghabike.model.data.RideWithFramesEntity
import ru.mrlargha.larghabike.model.data.TelemetryFrameEntity
import ru.mrlargha.larghabike.model.domain.Ride
import ru.mrlargha.larghabike.model.domain.RideWithFrames
import ru.mrlargha.larghabike.model.domain.TelemetryFrame
import ru.mrlargha.larghabike.getOrAwaitValue
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class DatabaseRideRepositoryTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var dao: RideDAO

    private lateinit var repository: DatabaseRideRepository

    @Before
    fun setUp() {
        repository = DatabaseRideRepository(dao)
    }

    @Test
    fun getAllRides() {
        val liveData = MutableLiveData(rideEntities)
        whenever(dao.getAllRides()).thenReturn(liveData)

        val result = repository.getAllRides()

        assertThat(result.getOrAwaitValue()).isEqualTo(rides)
        verify(dao, times(1)).getAllRides()
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun saveRide() {
        whenever(dao.insertRide(rideEntity)).thenReturn(Single.just(rideEntity.bikeId))

        val result = repository.saveRide(ride)

        assertThat(result.blockingGet()).isEqualTo(rideEntity.id)
        verify(dao, times(1)).insertRide(rideEntity)
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun updateRide() {
        whenever(dao.updateRide(rideEntity)).thenReturn(Completable.complete())

        repository.updateRide(ride)

        verify(dao, times(1)).updateRide(rideEntity)
    }

    @Test
    fun getAllFinishedRides() {
        val liveData = MutableLiveData(rideEntities)
        whenever(dao.getAllFinishedRides()).thenReturn(liveData)

        val result = repository.getAllFinishedRides()

        assertThat(result.getOrAwaitValue()).isEqualTo(rides)
        verify(dao, times(1)).getAllFinishedRides()
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun getRideById() {
        whenever(dao.getRideById(rideEntity.id)).thenReturn(Single.just(rideEntity))

        val result = repository.getRideById(ride.id)

        assertThat(result.blockingGet()).isEqualTo(ride)
        verify(dao, times(1)).getRideById(ride.id)
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun addFrame() {
        whenever(dao.insertTelemetryFrame(frameEntity)).thenReturn(Completable.complete())

        repository.addFrame(frame)

        verify(dao, times(1)).insertTelemetryFrame(frameEntity)
        verifyNoMoreInteractions(dao)
    }

    @Test
    fun getRideWithFrames() {
        whenever(dao.getRideWithFramesById(rideWithFrames.ride.id)).thenReturn(
            Single.just(
                rideWithFramesEntity
            )
        )

        val result = repository.getRideWithFrames(rideWithFrames.ride.id)

        assertThat(result.blockingGet()).isEqualTo(rideWithFrames)
        verify(dao, times(1)).getRideWithFramesById(rideWithFrames.ride.id)
    }

    @Test
    fun getAllFramesByRide() {
        whenever(dao.getAllFramesByRide(ride.id)).thenReturn(Single.just(frameEntities))

        val result = repository.getAllFramesByRide(ride.id)

        // Допустим что мы не будем тестировать то, что у нас обновляется списочек во Flowable
        assertThat(result.blockingGet()).isEqualTo(frames)
        verify(dao, times(1)).getAllFramesByRide(ride.id)
    }

    companion object {
        const val timesample = 1012298400L * 1000
        val ride =
            Ride(

                1,
                Date().also { it.time = timesample - 60 * 60 * 1000 },
                Date().also { it.time = timesample },
                0,
                50f,
                25f,
                25f,
                25000f,
                true,
                1
            )

        val rideEntity = RideEntity(
            1,
            Date().also { it.time = timesample - 60 * 60 * 1000 },
            Date().also { it.time = timesample },
            0,
            50f,
            25f,
            25f,
            25000f,
            true,
            1
        )

        val frame = TelemetryFrame(
            1,
            ride.id,
            Date().apply { time = timesample },
            10f,
            10f,
            10,
            0
        )

        val frameEntity = TelemetryFrameEntity(
            1,
            rideEntity.id,
            Date().apply { time = timesample },
            10f,
            10f,
            10,
            0
        )

        val rides = listOf(ride, ride, ride)
        val rideEntities = listOf(rideEntity, rideEntity, rideEntity)
        val frames = generateFrames()
        val frameEntities = generateFrameEntities()
        val rideWithFrames = RideWithFrames(ride, frames)
        val rideWithFramesEntity = RideWithFramesEntity(rideEntity, frameEntities)

        private fun generateFrames(): List<TelemetryFrame> {
            val resultList = mutableListOf<TelemetryFrame>()
            repeat(10) {
                resultList.add(
                    frame.copy(id = it)
                )
            }
            return resultList
        }

        private fun generateFrameEntities(): List<TelemetryFrameEntity> {
            val resultList = mutableListOf<TelemetryFrameEntity>()
            repeat(10) {
                resultList.add(
                    frameEntity.copy(id = it)
                )
            }
            return resultList
        }
    }

}