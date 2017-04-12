package io.duna.eventbus

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import io.duna.concurrent.EventLoopGroup
import net.jodah.concurrentunit.Waiter
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

object LocalEventBusSpec : Spek({

  describe ("an event bus") {
    val eventLoop = EventLoopGroup()
    val workerPool = Executors.newSingleThreadExecutor()
    val eventBus = LocalEventBus(eventLoop, workerPool)

    on ("subscribing to and emitting an event") {
      val waiter = Waiter()

      eventBus.subscribe<Unit>("event1") {
        onNext { waiter.resume() }
      }

      eventBus.subscribe<Unit>("event2") {
        onNext {
          waiter.fail("Routed an event to the wrong subscriber.")
          waiter.resume()
        }
      }

      it ("should route an event to the correct subscriber") {
        eventBus.emit<Unit>("event1").dispatch()
        waiter.await(1, TimeUnit.SECONDS)
      }
    }

    on ("creating subscriber handler after event emission") {
      val subscriber = eventBus.subscribe<Unit>("event1")
      eventBus.emit<Unit>("event1").dispatch()

      it ("should buffer the messages received and process once the handler is created") {
        val waiter = Waiter()
        subscriber.onNext { waiter.resume() }
        waiter.await(1, TimeUnit.SECONDS)
      }
    }

    on ("emitting an error") {
      val subscriber = eventBus.subscribe<Unit>("event1")
      eventBus.emit<Unit>("event1").dispatchError(Exception())

      it ("should execute the subscriber 'onError' handler") {
        val waiter = Waiter()

        subscriber.onNext {
          waiter.fail("Called onNext instead of onError")
          waiter.resume()
        }

        subscriber.onError { waiter.resume() }

        waiter.await(1, TimeUnit.SECONDS)
      }
    }

    on ("emitting events from multiple producers to a single threaded subscriber") {
      val subscriber = eventBus.subscribe<Int>("single-threaded")
      val counter = AtomicInteger(0)
      val waiter = Waiter()

      subscriber.onNext {
        waiter.assertTrue(counter.compareAndSet(it.attachment!!, it.attachment!! + 1))
        waiter.resume()
      }

      it ("should process the events sequentially and in order") {
        thread {
          for (i in 0..10) {
            eventBus.emit<Int>("single-threaded").dispatch(i)
          }
        }

        waiter.await(1, TimeUnit.SECONDS, 11)
      }
    }

    on ("emitting a request") {
      val waiter = Waiter()

      eventBus.subscribe<Unit>("request") {
        onNext {
          if (it.responseEvent == null) {
            waiter.fail("The message didn't arrive with a response event.")
            return@onNext
          }

          eventBus.emit<String>(it.responseEvent!!).dispatch("reply")
        }
      }

      it ("should receive a response") {
        eventBus
          .emit<Unit>("request")
          .request<Int>(null) {
            onNext {
              waiter.assertEquals("reply", it.attachment)
              waiter.resume()
            }
          }

        waiter.await(1, TimeUnit.SECONDS)
      }
    }

    on ("emitting an event without subscribers") {
      eventBus.subscribe<Unit>("test")
      eventBus.subscribe<Unit>("test")
      eventBus.subscribe<Unit>("test")

      eventBus.removeAll("test")

      it ("should throw an exception stating that no subscribers exist") {
        assertThat({eventBus.emit<Unit>("test").dispatch()}, throws<EmptySubscriberListException>())
      }
    }

    afterGroup {
      eventLoop.shutdownGracefully()
    }
  }
})
