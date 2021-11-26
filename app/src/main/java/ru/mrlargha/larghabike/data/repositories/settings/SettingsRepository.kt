package ru.mrlargha.larghabike.data.repositories.settings

import android.content.SharedPreferences
import ru.mrlargha.larghabike.domain.repos.ISettingsRepository
import javax.inject.Inject

/**
 * Провайдер настроек приложения
 *
 * @param sharedPreferences sp куда сохранять настройки
 */
class SettingsRepository @Inject constructor(private val sharedPreferences: SharedPreferences) :
    ISettingsRepository {

    /**
     * Установить id выбранного велосипеда
     *
     * @param id id велосипеда который надо выбрать
     */
    override fun setSelectedBikeId(id: Long) {
        sharedPreferences.edit().putLong(SELECTED_BIKE_ID, id).apply()
    }

    /**
     * Получить id выбранного велосипеда
     *
     * @return id выбранного велосипеда или -1 если такого нет
     */
    override fun getSelectedBikeId(): Long {
        return sharedPreferences.getLong(SELECTED_BIKE_ID, -1L)
    }

    companion object {
        private const val SELECTED_BIKE_ID = "SELECTED_BIKE_ID"
    }
}