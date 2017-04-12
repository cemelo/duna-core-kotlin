package io.duna.eventbus.event

import io.duna.eventbus.message.Message
import java.util.function.BiConsumer

interface Emitter<T> {

  val event: String

  val headers: Map<String, String>

  fun onDeadLetter(consumer: (Message<T>, Throwable) -> Unit) = onDeadLetter(BiConsumer(consumer))

  fun onDeadLetter(consumer: BiConsumer<in Message<T>, Throwable>): Emitter<T>

  fun withHeader(key: String, value: String): Emitter<T>

  fun dispatch(): Emitter<T> = dispatch(null)

  fun dispatch(attachment: T?): Emitter<T>

  fun dispatchError(throwable: Throwable): Emitter<T>

  fun <V> request(attachment: T?, init: Subscriber<V>.() -> Unit): Subscriber<V> {
    val subscriber = request<V>(attachment)
    subscriber.init()
    return subscriber
  }

  fun <V> request(init: Subscriber<V>.() -> Unit): Subscriber<V> = request(null, init)

  fun <V> request(attachment: T?): Subscriber<V>

  fun <V> request(): Subscriber<V> = request(null as T)

  infix fun header(key: String) = key

  infix fun String.to(value: String) = withHeader(this, value)
}
