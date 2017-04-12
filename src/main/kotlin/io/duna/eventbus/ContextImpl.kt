package io.duna.eventbus

class ContextImpl(override val eventBus: EventBus) : Context, HashMap<String, Any>()
