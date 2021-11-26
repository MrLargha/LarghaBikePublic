package ru.mrlargha.larghabike.domain.repos

import java.io.File

interface IFilesystemProvider {
    /**
     * Получить директорию внешнего хранилища
     *
     * @param type MIME-тип хранимых файлов
     * @return файл который является каталогом для хранения файлов данного типа или `null`
     */
    fun getExternalStorage(type: String? = null): File?
}