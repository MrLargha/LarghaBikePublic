package ru.mrlargha.larghabike.rx

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 * Schedulers Facade для тестов, использует только [Schedulers.trampoline()]
 *
 */
class TestSchedulersFacade : ISchedulersFacade {
    override fun ui(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun io(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun computation(): Scheduler {
        return Schedulers.trampoline()
    }
}