package io.duna.eventbus

class EmptySubscriberListException(message: String? = null,
                                   cause: Throwable? = null,
                                   enableSuppression: Boolean = false,
                                   writableStackTrace: Boolean = false)
  : RuntimeException(message, cause, enableSuppression, writableStackTrace)
