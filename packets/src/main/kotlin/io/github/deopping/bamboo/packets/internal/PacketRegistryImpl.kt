package io.github.deopping.bamboo.packets.internal

import io.github.deopping.bamboo.packets.api.PacketEvent
import io.github.deopping.bamboo.packets.api.PacketEventHandler
import io.github.deopping.bamboo.packets.api.PacketListener
import io.github.deopping.bamboo.packets.api.PacketRegistration
import io.github.deopping.bamboo.packets.api.PacketRegistry
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.IdentityHashMap

/**
 * @author DeOpping
 * @since 0.1.0
 */
internal class PacketRegistryImpl : PacketRegistry {

    private val lock = Any()

    private val registrations =
        IdentityHashMap<PacketListener, PacketRegistrationImpl>()

    private var handlers: List<PacketHandlerMethod> = emptyList()
    private var nextRegistrationOrder = 0L

    override fun register(listener: PacketListener): PacketRegistration =
        synchronized(lock) {
            registrations[listener]?.let { existing ->
                if (existing.isRegistered) {
                    return@synchronized existing
                }
            }

            // Validate and discover everything before changing registry state.
            val discoveredHandlers = discoverHandlers(listener)

            val registration = PacketRegistrationImpl(
                listener = listener,
                registry = this
            )

            registrations[listener] = registration
            handlers = (handlers + discoveredHandlers.map { handler ->
                handler.copy(registrationOrder = nextRegistrationOrder++)
            }).sortedWith(compareBy<PacketHandlerMethod> { it.priority.ordinal }
                .thenBy { it.registrationOrder }
            )

            registration
        }

    internal fun unregister(registration: PacketRegistrationImpl) {
        synchronized(lock) {
            val listener = registration.listener

            if (registrations[listener] !== registration) {
                return
            }

            registrations.remove(listener)
            handlers = handlers.filterNot { it.listener === listener }
            registration.deactivate()
        }
    }

    internal fun snapshotHandlers(): List<PacketHandlerMethod> =
        synchronized(lock) {
            handlers
        }

    private fun discoverHandlers(
        listener: PacketListener
    ): List<PacketHandlerMethod> {
        val methods = generateSequence(listener.javaClass as Class<*>?) { type ->
            type.superclass
        }
            .takeWhile { it != Any::class.java }
            .flatMap { type -> type.declaredMethods.asSequence() }
            .filterNot { it.isBridge || it.isSynthetic }
            .distinctBy { method -> method.name to method.parameterTypes.toList() }
            .sortedWith(
                compareBy<Method> { it.name }
                    .thenBy { it.parameterTypes.joinToString { type -> type.name } }
            )
            .toList()

        return methods.mapNotNull { method ->
            val annotation = method.getAnnotation(PacketEventHandler::class.java)
                ?: return@mapNotNull null

            validateHandler(method)

            require(method.trySetAccessible()) {
                "Cannot access packet handler '${method.name}' on " +
                        "'${listener.javaClass.name}'."
            }

            PacketHandlerMethod(
                listener = listener,
                method = method,
                eventType = method.parameterTypes.single(),
                priority = annotation.priority,
                ignoreCanceled = annotation.ignoreCancelled,
                registrationOrder = -1L
            )
        }
    }

    private fun validateHandler(method: Method) {
        require(method.parameterCount == 1) {
            "Packet handler '${method.name}' must accept exactly one parameter."
        }

        require(PacketEvent::class.java.isAssignableFrom(method.parameterTypes[0])) {
            "Parameter of packet handler '${method.name}' must implement PacketEvent."
        }

        require(method.returnType == Void.TYPE) {
            "Packet handler '${method.name}' must return Unit."
        }

        require(!Modifier.isStatic(method.modifiers)) {
            "Packet handler '${method.name}' must be an instance method."
        }
    }

}