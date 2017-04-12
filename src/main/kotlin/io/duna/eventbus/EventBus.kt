package io.duna.eventbus

import io.duna.eventbus.event.Emitter
import io.duna.eventbus.event.Subscriber

interface EventBus {

  fun <T> emit(event: String, init: Emitter<T>.() -> Unit): Emitter<T> {
    val emitter = emit<T>(event)
    emitter.init()
    return emitter
  }

  fun <T> emit(event: String): Emitter<T>

  fun <T> subscribe(event: String, init: Subscriber<T>.() -> Unit): Subscriber<T> {
    val subscriber = subscribe<T>(event)
    subscriber.init()
    return subscriber
  }

  fun <T> subscribe(event: String): Subscriber<T>

  fun remove(event: String, subscriber: Subscriber<*>)

  fun removeAll(event: String)

  fun execute(subscriber: Subscriber<*>, task: () -> Unit)

  fun executeBlocking(task: () -> Unit)

}
