package io.duna.eventbus.event

import io.duna.eventbus.message.Message
import io.reactivex.Flowable
import java.util.function.Consumer

interface Subscriber<T> {

  val event: String

  val isBlocking: Boolean

  fun onNext(consumer: (Message<T>) -> Unit) = onNext(Consumer(consumer))

  fun onNext(consumer: Consumer<in Message<T>>): Subscriber<T>

  fun onError(consumer: (Message<T>) -> Unit) = onError(Consumer(consumer))

  fun onError(consumer: Consumer<in Message<T>>): Subscriber<T>

  fun blocking(): Subscriber<T>

  fun cancel(): Unit

  fun toFlowable(): Flowable<T>

}
