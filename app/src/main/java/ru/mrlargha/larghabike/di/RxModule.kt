package ru.mrlargha.larghabike.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.mrlargha.larghabike.rx.ISchedulersFacade
import ru.mrlargha.larghabike.rx.SchedulersFacade

@Module
@InstallIn(SingletonComponent::class)
abstract class RxModule {
    @Binds
    abstract fun provideSchedulersFacade(schedulersFacade: SchedulersFacade): ISchedulersFacade
}