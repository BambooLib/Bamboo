package io.github.deopping.bamboo.event.internal

import io.github.deopping.bamboo.event.api.CancellableEvent
import io.github.deopping.bamboo.event.api.Event
import io.github.deopping.bamboo.event.api.EventExceptionHandler
import io.github.deopping.bamboo.event.api.EventHandler
import io.github.deopping.bamboo.event.api.EventListener
import io.github.deopping.bamboo.event.api.EventManager
import io.github.deopping.bamboo.event.api.EventPriority
import io.github.deopping.bamboo.event.api.EventSubscription
import io.github.deopping.bamboo.reflection.ReflectionApi
import io.github.deopping.bamboo.reflection.api.MethodAccessor
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.IdentityHashMap
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

internal class DefaultEventManager(
    private val executor: Executor,
    private val exceptionHandler: EventExceptionHandler,
) : EventManager {
    private val reflection: ReflectionApi = ReflectionApi.create()

    private val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()

    private val registrations: IdentityHashMap<EventListener, DefaultEventSubscription> =
        IdentityHashMap()

    private val handlers: MutableList<RegisteredEventHandler> = ArrayList()

    private var nextSequence: Long = 0

    override fun register(listener: EventListener): EventSubscription {
        val discoveredHandlers = discoverHandlers(listener)

        require(discoveredHandlers.isNotEmpty()) {
            "Listener '${listener.javaClass.name}' does not contain any valid event handlers."
        }

        return lock.write {
            check(!registrations.containsKey(listener)) {
                "Listener '${listener.javaClass.name}' is already registered."
            }

            val subscription =
                DefaultEventSubscription { registeredSubscription ->
                    unregister(registeredSubscription)
                }

            registrations[listener] = subscription

            discoveredHandlers.forEach { handler ->
                handlers.add(
                    RegisteredEventHandler(
                        listener = listener,
                        subscription = subscription,
                        eventType = handler.eventType,
                        accessor = handler.accessor,
                        priority = handler.priority,
                        ignoreCanceled = handler.ignoreCanceled,
                        sequence = nextSequence++,
                    ),
                )
            }

            handlers.sortWith(
                compareBy<RegisteredEventHandler> { it.priority.ordinal }
                    .thenBy { it.sequence },
            )

            subscription
        }
    }

    override fun <T : Event> callEvent(event: T): T {
        val matchingHandlers =
            lock.read {
                handlers.filter { handler ->
                    handler.eventType.isAssignableFrom(event.javaClass)
                }
            }

        val cancellableEvent = event as? CancellableEvent

        matchingHandlers.forEach { handler ->
            if (handler.ignoreCanceled && cancellableEvent?.isCanceled == true) {
                return@forEach
            }

            try {
                handler.accessor.invoke(handler.listener, event)
            } catch (exception: Throwable) {
                if (exception is VirtualMachineError ||
                    exception is LinkageError
                ) {
                    throw exception
                }

                if (exception is InterruptedException) {
                    Thread.currentThread().interrupt()
                }

                exceptionHandler.onException(
                    event = event,
                    listener = handler.listener,
                    exception = exception,
                )
            }
        }

        return event
    }

    override fun <T : Event> callEventAsync(event: T): CompletableFuture<T> =
        CompletableFuture.supplyAsync(
            { callEvent(event) },
            executor,
        )

    private fun unregister(subscription: DefaultEventSubscription): Boolean =
        lock.write {
            val registration =
                registrations.entries.firstOrNull { entry ->
                    entry.value === subscription
                } ?: return@write false

            registrations.remove(registration.key)

            handlers.removeAll { handler ->
                handler.subscription === subscription
            }

            subscription.markUnregistered()

            true
        }

    private fun discoverHandlers(listener: EventListener): List<PreparedEventHandler> =
        listener.javaClass.methods
            .asSequence()
            .filter { method ->
                method.isAnnotationPresent(EventHandler::class.java) &&
                    !method.isBridge &&
                    !method.isSynthetic
            }
            .sortedBy(Method::toGenericString)
            .map { method ->
                prepareHandler(method)
            }
            .toList()

    private fun prepareHandler(method: Method): PreparedEventHandler {
        require(!Modifier.isStatic(method.modifiers)) {
            "Event handler '${method.name}' must not be static."
        }

        require(method.parameterCount == 1) {
            "Event handler '${method.name}' must accept exactly one parameter."
        }

        val parameterType = method.parameterTypes.single()

        require(Event::class.java.isAssignableFrom(parameterType)) {
            "Event handler '${method.name}' must accept a type implementing Event."
        }

        require(method.returnType == Void.TYPE) {
            "Event handler '${method.name}' must return Unit."
        }

        val annotation = method.getAnnotation(EventHandler::class.java)

        return PreparedEventHandler(
            eventType = parameterType.asSubclass(Event::class.java),
            accessor = reflection.createMethodAccessor(method),
            priority = annotation.priority,
            ignoreCanceled = annotation.ignoreCanceled,
        )
    }

    private data class PreparedEventHandler(
        public val eventType: Class<out Event>,
        public val accessor: MethodAccessor,
        public val priority: EventPriority,
        public val ignoreCanceled: Boolean,
    )

    private data class RegisteredEventHandler(
        public val listener: EventListener,
        public val subscription: DefaultEventSubscription,
        public val eventType: Class<out Event>,
        public val accessor: MethodAccessor,
        public val priority: EventPriority,
        public val ignoreCanceled: Boolean,
        public val sequence: Long,
    )
}
