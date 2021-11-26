package ru.mrlargha.larghabike.data.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.mrlargha.larghabike.domain.repos.IFilesystemProvider
import java.io.File
import javax.inject.Inject

/**
 * Провайдер файловой системы (по сути обёртка над `context`)
 *
 * @property context конекст приложения
 */
class FilesystemProvider @Inject constructor(@ApplicationContext private val context: Context) :
    IFilesystemProvider {
    /**
     * Получить директорию внешнего хранилища
     *
     * @param type MIME-тип хранимых файлов
     * @return файл который является каталогом для хранения файлов данного типа или `null`
     */
    override fun getExternalStorage(type: String?): File? {
        return context.getExternalFilesDir(type)
    }
}