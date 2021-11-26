package ru.mrlargha.larghabike.data.repositories.pathutils

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil

/**
 * Оптимизатор пути, работает на базе [PolyUtil]
 */
object PathOptimizer {
    /**
     * Оптимизирует маршрут. Удаляет лишние промежуточные сегменты
     * В реализации использован алгоритм со сложностью O(n*n), поэтому это нельзя запускать на UI потоке
     * @param path путь, который нужно оптимизировать
     * @return оптимизированный маршрут
     */
    fun optimizePath(path: List<LatLng>): List<LatLng> {
//        assert(!Thread.currentThread().equals(Looper.getMainLooper().thread))
        return if (path.size > 3) PolyUtil.simplify(path.toList(), 5.0) else path
    }
}