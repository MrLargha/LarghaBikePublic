package ru.mrlargha.larghabike.data.repositories.mappreview

import com.google.android.gms.maps.model.LatLng
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class GMapsStaticAPIPathEncoderTest {

    @Test
    fun encode() {
        val result = GMapsStaticAPIPathEncoder.encode(path, "red", 1)

        assertThat(result).isEqualTo("color:red|weight:1|12.0,12.0|12.0,12.0|12.0,12.0")
    }

    companion object {
        val path = listOf(LatLng(12.0, 12.0), LatLng(12.0, 12.0), LatLng(12.0, 12.0))
    }

}