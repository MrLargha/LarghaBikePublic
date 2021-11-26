package ru.mrlargha.larghabike.data.database.typeconverters

import org.junit.*
import com.google.common.truth.Truth.*
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class DateConverterTest {

    private lateinit var converter: DateConverter

    @Before
    fun setUp() {
        converter = DateConverter()
    }

    @Test
    fun fromTimestamp() {
        val result = converter.fromTimestamp(dateUnix)

        assertThat(result).isEqualTo(date)
    }

    @Test
    fun dateToTimestamp() {
        val result = converter.dateToTimestamp(date)

        assertThat(result).isEqualTo(dateUnix)
    }

    @Test
    fun fromTimestampNull() {
        val result = converter.fromTimestamp(null)

        assertThat(result).isNull()
    }

    @Test
    fun dateToTimestampNull() {
        val result = converter.dateToTimestamp(null)

        assertThat(result).isNull()
    }

    companion object {
        private const val dateUnix = 1637694330L
        private val date = Date().apply { time = dateUnix }
    }

}