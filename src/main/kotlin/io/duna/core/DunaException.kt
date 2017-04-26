package io.duna.core

class DunaException(message: String? = null,
                    cause: Throwable? = null,
                    enableSuppression: Boolean = false,
                    writableStackTrace: Boolean = true)
  : RuntimeException(message, cause, enableSuppression, writableStackTrace)
