package ru.mrlargha.larghabike.domain.repos

interface IAPIKeysProvider {
    /**
     * Получить ключ API для Google карт
     *
     * @return ключ API
     */
    fun getMapsKey(): String
}