package ru.mrlargha.larghabike.data.util

import ru.mrlargha.larghabike.BuildConfig
import ru.mrlargha.larghabike.domain.repos.IURLRepository
import javax.inject.Inject

/**
 * Провайдеро адресов API
 *
 */
class URLRepository @Inject constructor() : IURLRepository {
    /**
     * Получить адрес статического API Google карт
     *
     * @return адрес API
     */
    override fun getStaticMapsApiUrl(): String = BuildConfig.GOOGLE_MAPS_STATIC_API_URL
}
