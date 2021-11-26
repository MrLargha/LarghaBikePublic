package ru.mrlargha.larghabike.presentation.ui.viewmodels

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Completable
import ru.mrlargha.larghabike.domain.repos.IBikeRepository
import ru.mrlargha.larghabike.model.domain.Bike
import ru.mrlargha.larghabike.rx.ISchedulersFacade
import java.io.File
import java.io.InputStream
import javax.inject.Inject

/**
 * ViewModel для BikeSetupActivity
 *
 * @property bikeRepository репозиторий с велосипедами
 * @property schedulersFacade фасад над JavaRX Schedulers
 */
@HiltViewModel
class BikeSetupViewModel @Inject constructor(
    private val bikeRepository: IBikeRepository,
    private val schedulersFacade: ISchedulersFacade,
) :
    BaseViewModel() {

    /**
     * Текст ошибки
     */
    val error: MutableLiveData<String> = MutableLiveData()

    /**
     * Добавлен ли велосипед
     */
    val isBikeAdded: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Добавить велосипед
     *
     * @param name название велосипеда
     * @param diameter диаметр колеса в дюймах
     * @param imageUri uri картинки
     */
    fun addBike(name: String, diameter: Float, imageUri: Uri?) {
        val trimmedName = name.trim()
        if (!checkBikeName(trimmedName)) {
            error.value = "Incorrect bike name"
            isBikeAdded.value = false
            return
        }
        if (!checkWheelDiameter(diameter)) {
            error.value = "Incorrect wheel diameter"
            isBikeAdded.value = false
            return
        }
        error.value = ""

        compositeDisposable.add(
            bikeRepository.saveBike(
                Bike(
                    name = trimmedName,
                    wheelDiameter = diameter,
                    photoPath = imageUri?.toString()
                )
            ).subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSuccess {
                    isBikeAdded.value = true
                    if(!bikeRepository.hasAvailableBike()){
                        bikeRepository.selectBike(it)
                    }
                }
                .subscribe()
        )
    }

    /**
     * Получить файл для записи изображения
     *
     * @return файл для записи изображения
     */
    fun getImageFile(): File {
        return bikeRepository.getFileForNewBikeImage()
    }

    /**
     * Скопировать stream в указанный uri
     *
     * @param stream stream откуда копировать
     * @param to uri куда копировать
     */
    fun copyStreamToUri(stream: InputStream, to: Uri) {
        Completable.fromCallable {
            val bytes = stream.readBytes()
            val fileOut = to.toFile()
            fileOut.writeBytes(bytes)
        }.subscribeOn(schedulersFacade.io()).observeOn(schedulersFacade.ui()).subscribe()
    }

    private fun checkBikeName(name: String): Boolean {
        return name.length in 5..25
    }

    private fun checkWheelDiameter(diameter: Float): Boolean {
        return diameter > 1f && diameter < 40f
    }
}