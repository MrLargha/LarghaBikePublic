package ru.mrlargha.larghabike.presentation.ui.viewmodels

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import ru.mrlargha.larghabike.domain.repos.IBikeRepository
import ru.mrlargha.larghabike.model.domain.Bike
import ru.mrlargha.larghabike.rx.SchedulersFacade
import ru.mrlargha.larghabike.rx.TestSchedulersFacade
import java.io.File
import java.io.InputStream


@RunWith(MockitoJUnitRunner::class)
class BikeSetupViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository: IBikeRepository

    @Mock
    lateinit var isAddedObserver: Observer<Boolean>

    @Mock
    lateinit var errorObserver: Observer<String>

    @Mock
    lateinit var inputStream: InputStream

    @Mock
    lateinit var uri: Uri

    @Mock
    lateinit var file: File

    @Mock
    lateinit var lifecycleOwner: LifecycleOwner

    private var lifecycle: Lifecycle? = null
    private lateinit var viewModel: BikeSetupViewModel

    @Before
    fun setUp() {
        lifecycle = LifecycleRegistry(lifecycleOwner)
        viewModel = BikeSetupViewModel(repository, TestSchedulersFacade())
        viewModel.isBikeAdded.observeForever(isAddedObserver)
        viewModel.error.observeForever(errorObserver)
    }

    @Test
    fun addBikeWithFailName() {
        for (name in nameFail) {
            viewModel.addBike(name, diameterOk.first(), null)

            verify(errorObserver, times(1)).onChanged(any())
            verify(isAddedObserver, times(1)).onChanged(false)
            verifyNoMoreInteractions(errorObserver)
            verifyNoMoreInteractions(isAddedObserver)
            reset(errorObserver, isAddedObserver)
        }
    }

    @Test
    fun addBikeWithFailDiameter() {
        for (diameter in diameterFail) {
            viewModel.addBike(nameOk.first(), diameter, null)
            verify(errorObserver, times(1)).onChanged(any())
            verify(isAddedObserver, times(1)).onChanged(false)
            verifyNoMoreInteractions(errorObserver)
            verifyNoMoreInteractions(isAddedObserver)
            reset(errorObserver, isAddedObserver)
        }
    }

    @Test
    fun addBikeBothFail() {
        for (name in nameFail) {
            for (diameter in diameterFail) {
                viewModel.addBike(name, diameter, null)
                verify(errorObserver, times(1)).onChanged(any())
                verify(isAddedObserver, times(1)).onChanged(false)
                verifyNoMoreInteractions(errorObserver)
                verifyNoMoreInteractions(isAddedObserver)
                reset(errorObserver, isAddedObserver)
            }
        }
    }

    @Test
    fun addBikeSuccessHasSelected() {
        for (name in nameOk) {
            for (diameter in diameterOk) {
                whenever(repository.saveBike(any())).thenReturn(Single.just(1))
                whenever(repository.hasAvailableBike()).thenReturn(true)
                val bike = Bike(0, name, diameter, null)

                viewModel.addBike(bike.name, bike.wheelDiameter, null)

                verify(isAddedObserver, times(1)).onChanged(true)
                verify(errorObserver, times(1)).onChanged("")
                verify(repository, times(1)).saveBike(bike.copy(name = name.trim()))
                verify(repository, times(1)).hasAvailableBike()
                verifyNoMoreInteractions(repository, isAddedObserver, errorObserver)
                reset(errorObserver, isAddedObserver, repository)
            }
        }
    }

    @Test
    fun addBikeSuccessWithoutSelected() {
        for (name in nameOk) {
            for (diameter in diameterOk) {
                whenever(repository.saveBike(any())).thenReturn(Single.just(1))
                whenever(repository.hasAvailableBike()).thenReturn(false)
                val bike = Bike(0, name, diameter, null)

                viewModel.addBike(bike.name, bike.wheelDiameter, null)

                verify(isAddedObserver, times(1)).onChanged(true)
                verify(errorObserver, times(1)).onChanged("")
                verify(repository, times(1)).saveBike(bike.copy(name = name.trim()))
                verify(repository, times(1)).hasAvailableBike()
                verify(repository, times(1)).selectBike(1)
                verifyNoMoreInteractions(repository, isAddedObserver, errorObserver)
                reset(errorObserver, isAddedObserver, repository)
            }
        }
    }

    @Test
    fun getImageFile() {
        whenever(repository.getFileForNewBikeImage()).thenReturn(file)

        viewModel.getImageFile()

        verify(repository, times(1)).getFileForNewBikeImage()
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun copyStreamToUri() {
        // Моки под этот тест я написал, но смысла в нем нет, т.к. он просто проверит что у
        // stream и uri были вызваны нужные методы
    }

    companion object {
        val nameFail = listOf(
            "Fail",
            "1",
            "",
            " ",
            "           112",
            "         ",
            "123         ",
            "KEK".repeat(100)
        )

        val nameOk = listOf(
            "OkName", "Nice name", "      Great Name       "
        )

        val diameterFail = listOf(
            -1f,
            -10000f,
            Float.MIN_VALUE,
            Float.MAX_VALUE,
            -0.00001f,
            10000f,
            41f,
            40.000001f,
            0.00000001f,
            0.99999f
        )

        val diameterOk = listOf(
            1.1f, 21f, 24f, 28f, 30f, 39.9f
        )
    }
}