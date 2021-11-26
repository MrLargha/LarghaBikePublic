package ru.mrlargha.larghabike.presentation.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import ru.mrlargha.larghabike.domain.repos.IBikeRepository
import ru.mrlargha.larghabike.rx.SchedulersFacade
import androidx.lifecycle.Lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import ru.mrlargha.larghabike.model.domain.Bike


@RunWith(MockitoJUnitRunner::class)
class BikeSelectViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var bikeRepository: IBikeRepository


    @Mock
    lateinit var bikeListObserver: Observer<List<Bike>>

    @Mock
    lateinit var selectedBikeObserver: Observer<List<Bike>>

    @Mock
    var lifecycleOwner: LifecycleOwner? = null
    var lifecycle: Lifecycle? = null

    @Before
    fun setUp() {

    }

    @Test
    fun selectBike() {
    }
}