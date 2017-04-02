package io.duna.eventbus.event

import io.duna.eventbus.Message
import java.util.function.BiConsumer
import java.util.function.Consumer

fun <T, V> Emitter<T>.request(attachment: T?, init: Subscriber<V>.() -> Unit): Subscriber<V> {
  val subscriber = request<V>(attachment)
  subscriber.init()
  return subscriber
}

fun <T> Emitter<T>.onError(consumer: (Message<T>, Throwable) -> Unit) =
  onError(BiConsumer { m: Message<T>, t: Throwable -> consumer.invoke(m, t) })

fun <T> Emitter<T>.onDeadLetter(consumer: (Message<T>) -> Unit) =
  onDeadLetter(Consumer(consumer::invoke))
