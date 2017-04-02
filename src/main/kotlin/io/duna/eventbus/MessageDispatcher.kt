package io.duna.eventbus

interface MessageDispatcher {

  fun <T> dispatch(message: Message<T>)
}
