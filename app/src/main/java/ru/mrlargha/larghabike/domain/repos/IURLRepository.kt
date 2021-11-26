package ru.mrlargha.larghabike.domain.repos

interface IURLRepository {
    /**
     * Получить адрес статического API Google карт
     *
     * @return адрес API
     */
    fun getStaticMapsApiUrl(): String
}