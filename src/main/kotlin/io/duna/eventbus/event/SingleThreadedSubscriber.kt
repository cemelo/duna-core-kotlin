package io.duna.eventbus.event

import io.duna.eventbus.EventBus
import java.util.concurrent.atomic.AtomicBoolean

internal class SingleThreadedSubscriber<T>(event: String,
                                           eventBus: EventBus)
  : DefaultSubscriber<T>(event, eventBus) {

  private val running = AtomicBoolean(false)

  val isRunning: Boolean
    get() = running.get()

  fun beginExecution() = running.compareAndSet(false, true)

  fun endExecution() = running.compareAndSet(true, false)

}
