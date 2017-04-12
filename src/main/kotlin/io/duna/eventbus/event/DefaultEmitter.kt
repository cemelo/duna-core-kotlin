package io.duna.eventbus.event

import io.duna.eventbus.EventBus
import io.duna.eventbus.message.Message
import io.duna.eventbus.message.MessageDispatcher
import io.duna.eventbus.message.MessageImpl
import java.util.*
import java.util.function.BiConsumer

internal class DefaultEmitter<T>(override val event: String,
                                 private val eventBus: EventBus,
                                 private val dispatcher: MessageDispatcher) : Emitter<T> {

  override val headers = HashMap<String, String>()

  private var deadLetterConsumer: BiConsumer<in Message<T>, Throwable>? = null

  override fun withHeader(key: String, value: String): Emitter<T> {
    headers[key] = value
    return this
  }

  override fun dispatch(attachment: T?): Emitter<T> {
    val message = MessageImpl(target = event,
      headers = HashMap(headers),
      attachment = attachment)

    dispatcher.dispatch(message)
    return this
  }

  override fun dispatchError(throwable: Throwable): Emitter<T> {
    val message = MessageImpl<T>(target = event,
      headers = HashMap(headers),
      cause = throwable)

    dispatcher.dispatch(message)
    return this
  }

  override fun <V> request(attachment: T?): Subscriber<V> {
    val message = MessageImpl<T>(target = event,
      headers = HashMap(headers),
      attachment = attachment,
      responseEvent = UUID.randomUUID().toString())

    val subscriber = eventBus.subscribe<V>(message.responseEvent!!)
    dispatcher.dispatch(message)

    return subscriber
  }

  override fun onDeadLetter(consumer: BiConsumer<in Message<T>, Throwable>): Emitter<T> {
    deadLetterConsumer = consumer
    return this
  }
}
