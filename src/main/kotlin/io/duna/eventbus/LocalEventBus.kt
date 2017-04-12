package io.duna.eventbus

import io.duna.eventbus.event.*
import io.duna.eventbus.message.Message
import io.duna.eventbus.message.MessageConsumer
import io.duna.eventbus.message.MessageDispatcher
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.ExecutorService

open class LocalEventBus(private val eventPool: ExecutorService,
                         private val workerPool: ExecutorService)
  : EventBus, MessageDispatcher, MessageConsumer {

  private val eventSubscribersMap: MutableMap<String, MutableSet<Subscriber<*>>> = ConcurrentSkipListMap()
  private val availableSubscribers: MutableSet<Subscriber<*>> = Collections.newSetFromMap(ConcurrentHashMap())

  private val executionContext: MutableMap<Subscriber<*>, Context> = ConcurrentHashMap()

  override fun <T> emit(event: String): Emitter<T> = DefaultEmitter(event, this, this)

  override fun <T> subscribe(event: String): Subscriber<T> {
    val subscriber = SingleThreadedSubscriber<T>(event, this)

    eventSubscribersMap.computeIfAbsent(event) {
      Collections.newSetFromMap<Subscriber<*>>(ConcurrentHashMap())
    } += subscriber

    availableSubscribers += subscriber

    return subscriber
  }

  override fun remove(event: String, subscriber: Subscriber<*>) {
    if (availableSubscribers.remove(subscriber) && eventSubscribersMap.containsKey(event)) {
      eventSubscribersMap[event]?.remove(subscriber)
    }
  }

  override fun removeAll(event: String) {
    val oldSubscribers = eventSubscribersMap.remove(event)

    if (oldSubscribers != null) {
      availableSubscribers.removeAll(oldSubscribers)
    }
  }

  override fun execute(subscriber: Subscriber<*>, task: () -> Unit) {
    val context = executionContext.computeIfAbsent(subscriber) { ContextImpl(this) }

    if (subscriber.isBlocking) {
      workerPool.submit(task)
      return
    }

    if (subscriber is SingleThreadedSubscriber) {
      eventPool.execute(Runnable {
        if (!subscriber.beginExecution()) {
          this@LocalEventBus.execute(subscriber, task)

          Thread.yield()
          return@Runnable
        }

        Context.currentContextHolder.set(context)
        task.invoke()

        if (!subscriber.endExecution()) {
          throw RuntimeException("Subscriber currently not running.")
        }
      })
    } else {
      eventPool.execute(task)
    }
  }

  override fun executeBlocking(task: () -> Unit) {
    workerPool.execute(task)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T> accept(message: Message<T>) {
    var subscriberExists = false
    eventSubscribersMap[message.target]?.forEach { subscriber ->
      if (!availableSubscribers.contains(subscriber)) return@forEach
      subscriberExists = true

      this.execute(subscriber as DefaultSubscriber<T>) {
        subscriber.accept(message)
      }
    } ?: throw EmptySubscriberListException("No subscribers registered for event ${message.target}.")

    if (!subscriberExists)
      throw EmptySubscriberListException("No subscribers registered for event ${message.target}.")
  }

  override fun <T> dispatch(message: Message<T>) {
    accept(message)
  }
}

