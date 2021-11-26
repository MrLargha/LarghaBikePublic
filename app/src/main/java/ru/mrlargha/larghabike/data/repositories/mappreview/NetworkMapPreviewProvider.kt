package ru.mrlargha.larghabike.data.repositories.mappreview

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.mrlargha.larghabike.data.repositories.pathutils.PathOptimizer
import ru.mrlargha.larghabike.data.util.APIKeysProvider
import ru.mrlargha.larghabike.data.util.URLRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Провайдер для получения с Google Maps Static API превью карты
 *
 * @property httpClient [OkHttpClient] с которого будут выполняться запросы
 * @property apiKeysProvider провайдер ключей API
 * @property urlRepository провайдеро адресов серверов API
 */
@Singleton
class NetworkMapPreviewProvider @Inject constructor(
    private val httpClient: OkHttpClient,
    private val apiKeysProvider: APIKeysProvider,
    private val urlRepository: URLRepository
) : INetworkMapPreviewProvider {
    /**
     * Получить карту с маршрутом для пути
     *
     * @param path путь
     * @param sizeX ширина картинки, максимум 600, значения больше игнорируются
     * @param sizeY высота картинки, максимум 600, значения больше игнорируются
     * @return [Single] с картинкой
     */
    override fun getPreview(path: List<LatLng>, sizeX: Int, sizeY: Int): Single<Bitmap> {
        return Single.fromCallable {
            val optimizedPath = PathOptimizer.optimizePath(path)
            val pathPart = GMapsStaticAPIPathEncoder.encode(optimizedPath)
            val urlFormat = "%s?size=%dx%d&maptype=roadmap&path=%s&key=%s"
            val url = String.format(
                urlFormat,
                urlRepository.getStaticMapsApiUrl(),
                sizeX,
                sizeY,
                pathPart,
                apiKeysProvider.getMapsKey()
            )
            val request = Request.Builder().url(url).build()
            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful && response.body() != null) {
                throw IOException("Response code: ${response.code()}. ${response.message()}")
            }
            val dateBytes = response.body()?.bytes() ?: byteArrayOf()
            return@fromCallable BitmapFactory.decodeByteArray(dateBytes, 0, dateBytes.size)
        }
    }
}