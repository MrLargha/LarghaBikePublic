package ru.mrlargha.larghabike.data.repositories.bike

import org.junit.*
import com.google.common.truth.Truth.*
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.runner.RunWith
import org.mockito.Mock
import kotlin.test.assertFails
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import ru.mrlargha.larghabike.data.database.BikeDAO
import ru.mrlargha.larghabike.model.data.BikeEntity
import ru.mrlargha.larghabike.data.repositories.settings.SettingsRepository
import ru.mrlargha.larghabike.data.util.FilesystemProvider
import ru.mrlargha.larghabike.model.domain.Bike
import java.io.File
import java.lang.RuntimeException

@RunWith(MockitoJUnitRunner::class)
class BikeRepositoryTest {

    @Mock
    lateinit var filesystemProvider: FilesystemProvider

    @Mock
    lateinit var bikeProvider: BikeDAO

    @Mock
    lateinit var settingsRepository: SettingsRepository

    private lateinit var bikeRepository: BikeRepository


    @Before
    fun prepare() {
        bikeRepository = BikeRepository(filesystemProvider, bikeProvider, settingsRepository)
        imageFilesDir.mkdir()
    }

    @Test
    fun testSaveBike() {
        whenever(bikeProvider.addBike(any())).thenReturn(Single.just(3))

        val expectedResult = 3
        val bikeToSave = Bike(0, "Any name", 21f)

        val result = bikeRepository.saveBike(bikeToSave)

        assertThat(result.blockingGet()).isEqualTo(expectedResult)
        verify(bikeProvider, times(1)).addBike(any())
        verifyNoMoreInteractions(bikeProvider)
        verifyNoInteractions(filesystemProvider, settingsRepository)
    }

    @Test
    fun testGetBikeByID() {
        whenever(bikeProvider.getBikeById(any())).thenReturn(Single.just(bikeEntitySample))

        val result = bikeRepository.getBikeById(bikeSample.id)

        assertThat(result.blockingGet()).isEqualTo(bikeSample)
        verify(bikeProvider, times(1)).getBikeById(bikeSample.id)
        verifyNoMoreInteractions(bikeProvider)
        verifyNoInteractions(settingsRepository)
        verifyNoInteractions(filesystemProvider)
    }


    @Test
    fun testGetSelectedBike() {
        whenever(settingsRepository.getSelectedBikeId()).thenReturn(bikeSample.id)
        whenever(bikeProvider.getBikeById(bikeSample.id)).thenReturn(Single.just(bikeEntitySample))

        val result = bikeRepository.getSelectedBike()

        assertThat(result.blockingGet()).isEqualTo(bikeSample)
        verify(settingsRepository, times(1)).getSelectedBikeId()
        verifyNoMoreInteractions(settingsRepository)
        verify(bikeProvider, times(1)).getBikeById(bikeSample.id)
        verifyNoMoreInteractions(bikeProvider)
        verifyNoInteractions(filesystemProvider)
    }

    @Test
    fun testGetSelectedBikeWithoutSelectedBike() {
        whenever(settingsRepository.getSelectedBikeId()).thenReturn(-1)
        whenever(bikeProvider.getBikeById(-1)).thenThrow(RuntimeException())

        assertFails {
            val result = bikeRepository.getSelectedBike()
            result.blockingGet()
        }

        verify(settingsRepository, times(1)).getSelectedBikeId()
        verifyNoMoreInteractions(settingsRepository)
        verify(bikeProvider, times(1)).getBikeById(-1)
        verifyNoMoreInteractions(bikeProvider)
        verifyNoInteractions(filesystemProvider)
    }

    @Test
    fun testSelectBike() {
        bikeRepository.selectBike(bikeSample.id)

        verify(settingsRepository, times(1)).setSelectedBikeId(bikeSample.id)
        verifyNoMoreInteractions(settingsRepository)
        verifyNoInteractions(bikeProvider)
        verifyNoInteractions(filesystemProvider)
    }

    @Test
    fun testGetAllBikes() {
        whenever(bikeProvider.getAllBikes()).thenReturn(Observable.just(bikeSampleList))

        val result = bikeRepository.getAllBikes()

        assertThat(result.blockingIterable().first()).isEqualTo(list)
        verify(bikeProvider, times(1)).getAllBikes()
        verifyNoMoreInteractions(bikeProvider)
        verifyNoInteractions(filesystemProvider)
        verifyNoMoreInteractions(settingsRepository)
    }

    @Test
    fun testHasAvailableBikeWithAvailableBike() {
        whenever(settingsRepository.getSelectedBikeId()).thenReturn(1)

        val result = bikeRepository.hasAvailableBike()

        assertThat(result).isTrue()
        verify(settingsRepository, times(1)).getSelectedBikeId()
        verifyNoMoreInteractions(settingsRepository)
        verifyNoInteractions(bikeProvider)
        verifyNoInteractions(filesystemProvider)
    }

    @Test
    fun testHasAvailableBikeWithoutAvailableBike() {
        whenever(settingsRepository.getSelectedBikeId()).thenReturn(-1)

        val result = bikeRepository.hasAvailableBike()

        assertThat(result).isFalse()
        verify(settingsRepository, times(1)).getSelectedBikeId()
        verifyNoMoreInteractions(settingsRepository)
        verifyNoInteractions(bikeProvider)
        verifyNoInteractions(filesystemProvider)
    }

    @Test
    fun testGetFileForNewBikeImage() {
        whenever(filesystemProvider.getExternalStorage(any())).thenReturn(imageFilesDir)

        bikeRepository.getFileForNewBikeImage()

        verify(filesystemProvider, times(1)).getExternalStorage("image")
        verifyNoMoreInteractions(filesystemProvider)

        // Дичь
        assertThat(imageFilesDir.listFiles()).isNotEmpty()
        assertThat(imageFilesDir.listFiles()!!.size).isEqualTo(1)
        assertThat(imageFilesDir.listFiles()!!.first().name).isEqualTo("bikes")
        assertThat(imageFilesDir.listFiles()!!.first().listFiles()!!.size).isEqualTo(1)
        assertThat(imageFilesDir.listFiles()!!.first().listFiles()!!.first().name).contains(".png")
    }

    @After
    fun cleanup() {
        if (imageFilesDir.listFiles()?.isNotEmpty() == true)
            imageFilesDir.listFiles()?.forEach { it.deleteRecursively() }
    }

    companion object {
        val imageFilesDir = File("./image")
        val bikeZeroID = Bike(0, "Any name", 21f)
        val bikeSample = Bike(1, "Any name", 21f)

        val list = listOf(bikeSample, bikeSample, bikeSample)

        val bikeEntitySample = BikeEntity(1, "Any name", 21f)
        val bikeSampleList = listOf(bikeEntitySample, bikeEntitySample, bikeEntitySample)
    }

}


