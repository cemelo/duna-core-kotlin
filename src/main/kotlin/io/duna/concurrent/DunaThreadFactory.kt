package io.duna.concurrent

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class DunaThreadFactory : ThreadFactory {

  private val threadCounter = AtomicInteger(0)

  override fun newThread(r: Runnable): Thread {
    val thread = DunaThread("DunaThread-${threadCounter.getAndIncrement()}", r)
    thread.isDaemon = false

    return thread
  }
}
