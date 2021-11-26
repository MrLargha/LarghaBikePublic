package ru.mrlargha.larghabike.domain.repos

interface ISettingsRepository {
    /**
     * Установить id выбранного велосипеда
     *
     * @param id id велосипеда который надо выбрать
     */
    fun setSelectedBikeId(id: Long)

    /**
     * Получить id выбранного велосипеда
     *
     * @return id выбранного велосипеда или -1 если такого нет
     */
    fun getSelectedBikeId(): Long
}