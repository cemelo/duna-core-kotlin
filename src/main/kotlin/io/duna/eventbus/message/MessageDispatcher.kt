package io.duna.eventbus.message

interface MessageDispatcher {

  fun <T> dispatch(message: Message<T>)
}
