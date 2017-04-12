package io.duna.concurrent

import io.netty.util.concurrent.SingleThreadEventExecutor
import java.util.concurrent.Executor

class EventLoop(parent: EventLoopGroup,
                executor: Executor)
  : SingleThreadEventExecutor(parent, executor, true) {

  override fun run() {
    while (true) {
      val task = takeTask()
      if (task != null) {
        task.run()
        updateLastExecutionTime()
      }

      if (confirmShutdown()) {
        break
      }
    }
  }
}
