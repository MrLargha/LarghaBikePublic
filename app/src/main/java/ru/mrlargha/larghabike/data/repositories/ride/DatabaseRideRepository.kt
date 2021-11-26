package ru.mrlargha.larghabike.data.repositories.ride

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.mrlargha.larghabike.data.database.RideDAO
import ru.mrlargha.larghabike.domain.repos.IRideRepository
import ru.mrlargha.larghabike.model.converters.RideToRideEntityConverter
import ru.mrlargha.larghabike.model.converters.RideWithFramesToRideWithFramesEntityConverter
import ru.mrlargha.larghabike.model.converters.TelemetryFrameToTelemetryFrameEntityConverter
import ru.mrlargha.larghabike.model.domain.Ride
import ru.mrlargha.larghabike.model.domain.RideWithFrames
import ru.mrlargha.larghabike.model.domain.TelemetryFrame
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Хранит информацию о всех записанных поездках и базовую статистику в базе данных
 */
@Singleton
class DatabaseRideRepository @Inject constructor(
    private val rideDAO: RideDAO
) : IRideRepository {

    companion object {
        private const val TAG = "DatabaseRideRepository"
    }

    private val rideConverter = RideToRideEntityConverter()
    private val telemetryConverter = TelemetryFrameToTelemetryFrameEntityConverter()
    private val rideWithFramesConverter =
        RideWithFramesToRideWithFramesEntityConverter(rideConverter, telemetryConverter)

    override fun getAllRides(): LiveData<List<Ride>> {
        return Transformations.map(rideDAO.getAllRides()) { list ->
            list.map(rideConverter::convertReverse)
        }
    }

    override fun saveRide(ride: Ride): Single<Long> {
        return ride.let(rideConverter::convert).let(rideDAO::insertRide)
    }


    override fun updateRide(ride: Ride): Completable {
        return ride.let(rideConverter::convert).let(rideDAO::updateRide)
    }

    override fun getAllFinishedRides(): LiveData<List<Ride>> {
        return Transformations.map(rideDAO.getAllFinishedRides()) {
            it.map(rideConverter::convertReverse)
        }
    }

    override fun getRideById(rideId: Long): Single<Ride> {
        return rideDAO.getRideById(rideId).map(rideConverter::convertReverse)
    }

    override fun addFrame(frame: TelemetryFrame): Completable {
        return frame.let(telemetryConverter::convert).let(rideDAO::insertTelemetryFrame)
    }

    override fun getRideWithFrames(rideId: Long): Single<RideWithFrames> {
        return rideDAO.getRideWithFramesById(rideId).map(rideWithFramesConverter::convertReverse)
    }

    override fun getAllFramesByRide(rideId: Long): Single<List<TelemetryFrame>> {
        return rideDAO.getAllFramesByRide(rideId).map { it.map(telemetryConverter::convertReverse) }
    }

    override fun getCurrentRide(): Maybe<Ride> {
        return rideDAO.getCurrentRide().map(rideConverter::convertReverse)
    }
}