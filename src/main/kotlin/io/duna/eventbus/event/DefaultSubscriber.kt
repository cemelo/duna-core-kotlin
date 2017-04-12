package io.duna.eventbus.event

import io.duna.eventbus.Context
import io.duna.eventbus.EventBus
import io.duna.eventbus.message.Message
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import java.nio.BufferOverflowException
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer

internal open class DefaultSubscriber<T>(override val event: String,
                                         val eventBus: EventBus) : Subscriber<T> {

  private val MAX_BUFFER_SIZE = 1024

  private var messageConsumer: Consumer<in Message<T>>? = null
  private var errorConsumer: Consumer<in Message<T>>? = null

  private val pendingMessages: Queue<in Message<T>> = LinkedList()
  private val pendingErrorMessages: Queue<in Message<T>> = LinkedList()

  private val flowableEmitter = AtomicReference<FlowableEmitter<T>?>()

  final override var isBlocking: Boolean = false
    private set

  override fun blocking(): Subscriber<T> {
    isBlocking = true
    return this
  }

  @Suppress("UNCHECKED_CAST")
  override fun onNext(consumer: Consumer<in Message<T>>): Subscriber<T> {
    messageConsumer = consumer

    eventBus.execute(this) {
      while (pendingMessages.isNotEmpty()) {
        messageConsumer?.accept(pendingMessages.poll() as Message<T>)
      }
    }

    return this
  }

  @Suppress("UNCHECKED_CAST")
  override fun onError(consumer: Consumer<in Message<T>>): Subscriber<T> {
    errorConsumer = consumer

    eventBus.execute(this) {
      while (pendingErrorMessages.isNotEmpty()) {
        errorConsumer?.accept(pendingErrorMessages.poll() as Message<T>)
      }
    }

    return this
  }

  override fun cancel() {
    eventBus.remove(event, this)
  }

  override fun toFlowable(): Flowable<T> {
    TODO("not implemented")
  }

  open internal fun accept(message: Message<T>) {
    val targetConsumer =
      if (message.isError) errorConsumer
      else messageConsumer

    val targetQueue =
      if (message.isError) pendingErrorMessages
      else pendingMessages

    if (Context.isOnDunaThread()) {
      processMessage(targetConsumer, targetQueue, message)
    } else {
      eventBus.execute(this) {
        processMessage(targetConsumer, targetQueue, message)
      }
    }
  }

  private fun processMessage(consumer: Consumer<in Message<T>>?, queue: Queue<in Message<T>>, message: Message<T>) {
    consumer?.accept(message) ?:
      if (queue.size < MAX_BUFFER_SIZE)
        queue.offer(message)
      else throw BufferOverflowException()
  }
}
