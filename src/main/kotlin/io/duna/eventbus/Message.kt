package io.duna.eventbus

/**
 * Represents a message carried by the event bus.
 *
 * Messages are representations of events emitted by the application. They
 * are responsible for providing a context to enable the event bus routing.
 * They also carry an attachment which will be consumed by the subscribers.
 *
 * @author [Carlos Eduardo Melo][hk@cemelo.com]
 */
interface Message<out T> {

  val source: String?

  val target: String

  val responseEvent: String?

  val headers: Map<String, String>

  val attachment: T?

  val cause: Throwable?

  val isError: Boolean
    get() = cause != null

}
