package io.duna.eventbus.event

import io.duna.eventbus.Message
import java.util.function.BiConsumer
import java.util.function.Consumer

interface Emitter<T> {

  val eventName: String

  val headers: MutableMap<String, String>

  fun putHeader(key: String, value: String): Emitter<T>

  fun dispatch(attachment: T?): Emitter<T>

  fun dispatch(): Emitter<T> = dispatch(null)

  fun <V> request(attachment: T?): Subscriber<V>

  fun onError(consumer: BiConsumer<in Message<T>, Throwable>): Emitter<T>

  fun onDeadLetter(consumer: Consumer<in Message<T>>): Emitter<T>

}
