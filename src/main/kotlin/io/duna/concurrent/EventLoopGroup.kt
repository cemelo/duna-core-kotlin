package io.duna.concurrent

import io.netty.util.concurrent.EventExecutor
import io.netty.util.concurrent.MultithreadEventExecutorGroup
import java.util.concurrent.Executor
import java.util.concurrent.ThreadFactory

class EventLoopGroup(nThreads: Int = Runtime.getRuntime().availableProcessors(),
                     threadFactory: ThreadFactory = DunaThreadFactory())
  : MultithreadEventExecutorGroup(nThreads, threadFactory) {

  override fun newChild(executor: Executor, vararg args: Any?): EventExecutor {
    return EventLoop(this, executor)
  }
}
