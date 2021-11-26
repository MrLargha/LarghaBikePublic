package ru.mrlargha.larghabike.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.mrlargha.larghabike.data.database.typeconverters.DateConverter
import ru.mrlargha.larghabike.model.data.BikeEntity
import ru.mrlargha.larghabike.model.data.RideEntity
import ru.mrlargha.larghabike.model.data.TelemetryFrameEntity

/**
 * База данных приложения
 */
@Database(
    entities = [RideEntity::class, TelemetryFrameEntity::class, BikeEntity::class],
    version = 5
)
@TypeConverters(DateConverter::class)
abstract class RideDatabase : RoomDatabase() {
    /**
     * Получить DAO для работы с поездками и телеметрией
     *
     * @return [RideDAO]
     */
    abstract fun rideDao(): RideDAO

    /**
     * Получить DAO для работы с велосипедами
     *
     * @return [BikeDAO]
     */
    abstract fun bikeDao(): BikeDAO
}