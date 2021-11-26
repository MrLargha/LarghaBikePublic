package ru.mrlargha.larghabike.data.util

import ru.mrlargha.larghabike.BuildConfig
import ru.mrlargha.larghabike.domain.repos.IAPIKeysProvider
import javax.inject.Inject

/**
 * Провайдер ключей API
 *
 */
class APIKeysProvider @Inject constructor() : IAPIKeysProvider {

    companion object {
        private val mapKey = BuildConfig.GOOGLE_MAP_API_KEY
    }

    /**
     * Получить ключ API для Google карт
     *
     * @return ключ API
     */
    override fun getMapsKey(): String {
        return mapKey
    }
}
