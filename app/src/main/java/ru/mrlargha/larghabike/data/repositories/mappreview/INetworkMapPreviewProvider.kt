package ru.mrlargha.larghabike.data.repositories.mappreview

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single

/**
 * Провайдер для получения превью карты по сети
 */
interface INetworkMapPreviewProvider {
    /**
     * Получить карту с маршрутом для пути
     *
     * @param path путь
     * @param sizeX ширина картинки, максимум 600, значения больше игнорируются
     * @param sizeY высота картинки, максимум 600, значения больше игнорируются
     * @return [Single] с картинкой
     */
    fun getPreview(path: List<LatLng>, sizeX: Int, sizeY: Int): Single<Bitmap>
}