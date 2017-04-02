package io.duna.eventbus

import io.duna.eventbus.event.Emitter
import io.duna.eventbus.event.Subscriber

fun <T> EventBus.emit(event: String, init: Emitter<T>.() -> Unit) {
  return emit<T>(event).init()
}

fun <T> EventBus.subscribe(event: String, init: Subscriber<T>.() -> Unit) {
  return subscribe<T>(event).init()
}

operator fun <T> Message<T>.get(key: String) = headers[key]
