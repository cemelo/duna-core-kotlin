package io.duna.eventbus

import io.duna.concurrent.DunaThread

interface Context : Map<String, Any> {

  val currentContext: Context
    get() = currentContextHolder.get()

  val eventBus: EventBus

  companion object {

    internal val currentContextHolder: ThreadLocal<Context> = ThreadLocal()

    fun isOnDunaThread(): Boolean {
      return Thread.currentThread() is DunaThread
    }
  }
}
