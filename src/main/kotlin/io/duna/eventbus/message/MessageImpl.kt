package io.duna.eventbus.message

data class MessageImpl<out T>(override val source: String? = null,
                              override val target: String,
                              override val responseEvent: String? = null,
                              override val headers: Map<String, String> = emptyMap(),
                              override val attachment: T? = null,
                              override val cause: Throwable? = null) : Message<T>
