package io.duna.eventbus.message

interface MessageConsumer {
  fun <T> accept(message: Message<T>)
}
