package io.github.deopping.bamboo.packets.internal

import io.github.deopping.bamboo.packets.api.PacketPriority
import java.lang.reflect.Method

/**
 * Stores metadata required to invoke a registered packet event handler.
 *
 * @property listener the object declaring the handler
 * @property method the handler method
 * @property eventType the event type accepted by the handler
 * @property priority the handler's invocation priority
 * @property ignoreCanceled whether canceled events should be ignored
 * @property registrationOrder the order in which this handler was registered
 * @author DeOpping
 * @since 0.1.0
 */
internal data class PacketHandlerMethod(
    val listener: Any,
    val method: Method,
    val eventType: Class<*>,
    val priority: PacketPriority,
    val ignoreCanceled: Boolean,
    val registrationOrder: Long
)
