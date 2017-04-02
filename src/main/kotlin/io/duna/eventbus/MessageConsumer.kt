package io.duna.eventbus

interface MessageConsumer {
  fun <T> accept(message: Message<T>)
}
