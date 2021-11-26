package ru.mrlargha.larghabike.data.repositories.tracking.calculation

import android.location.Location
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

/**
 * Очень большие у меня сомнения по поводу этого теста...
 */
@RunWith(MockitoJUnitRunner::class)
class GPSLocationFilterTest {

    @Mock
    lateinit var location: Location

    @Test
    fun filterLocation() {
        whenever(location.getSpeed()).thenReturn(speedToFilter)

        val location = GPSLocationFilter.filterLocation(location)

        verify(location, times(1)).speed
        verify(location, times(1)).speed = 0f
        verifyNoMoreInteractions(location)
    }

    @Test
    fun notFilterLocation() {
        whenever(location.getSpeed()).thenReturn(speedNotFilter)

        val location = GPSLocationFilter.filterLocation(location)

        verify(location, times(1)).getSpeed()
        verifyNoMoreInteractions(location)
    }

    companion object {
        const val speedNotFilter = 10f
        const val speedToFilter = 0.1f
    }

}