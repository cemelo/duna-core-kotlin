package io.duna.eventbus

import io.duna.eventbus.event.Emitter
import io.duna.eventbus.event.Subscriber

interface EventBus {

  fun <T> emit(event: String): Emitter<T>

  fun <T> subscribe(event: String): Subscriber<T>

  fun unsubscribe(event: String, subscriber: Subscriber<*>)

  fun removeAll(event: String)

}
