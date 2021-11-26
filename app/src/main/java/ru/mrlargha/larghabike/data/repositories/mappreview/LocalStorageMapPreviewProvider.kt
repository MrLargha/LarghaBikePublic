package ru.mrlargha.larghabike.data.repositories.mappreview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Completable
import io.reactivex.Single
import ru.mrlargha.larghabike.model.domain.Ride
import java.io.File
import javax.inject.Inject

/**
 * Локальный провайдер закэшированных картинок с маршрутом
 *
 * @property context контекст приложения (нужен для доступа к файлам)
 */
class LocalStorageMapPreviewProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : ILocalStorageMapPreviewProvider {

    companion object {
        const val TAG = "LocalStorageMapPreviewProvider"
    }

    override fun getMapPreview(ride: Ride): Single<Bitmap> {
        return Single.fromCallable {
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            BitmapFactory.decodeFile(context.cacheDir.path + '/' + ride.id.toString(), options)
        }
    }

    override fun hasPreviewForRide(rideId: Long): Boolean {
        return File(context.cacheDir.path + '/' + rideId.toString()).exists()
    }

    override fun saveMap(rideId: Long, bitmap: Bitmap): Completable {
        Log.i(TAG, "saveMap: saved map for ride $rideId")
        return Completable.fromCallable {
            val file = File(context.cacheDir, rideId.toString())
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, file.outputStream())
        }
    }
}