package io.duna.eventbus

import io.duna.eventbus.event.Emitter
import io.duna.eventbus.event.Subscriber

class LocalEventBus : EventBus, MessageDispatcher, MessageConsumer {

  override fun <T> emit(event: String): Emitter<T> {
    TODO("not implemented")
  }

  override fun <T> subscribe(event: String): Subscriber<T> {
    TODO("not implemented")
  }

  override fun unsubscribe(event: String, subscriber: Subscriber<*>) {
    TODO("not implemented")
  }

  override fun removeAll(event: String) {
    TODO("not implemented")
  }

  override fun <T> accept(message: Message<T>) {
    TODO("not implemented")
  }

  override fun <T> dispatch(message: Message<T>) {
    TODO("not implemented")
  }

}
