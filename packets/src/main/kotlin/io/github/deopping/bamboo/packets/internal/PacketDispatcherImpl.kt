package io.github.deopping.bamboo.packets.internal

import io.github.deopping.bamboo.packets.api.PacketDispatcher
import io.github.deopping.bamboo.packets.api.PacketEvent
import io.github.deopping.bamboo.packets.api.PacketView
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * @author DeOpping
 * @since 0.1.0
 */
internal class PacketDispatcherImpl(
    private val registry: PacketRegistryImpl,
    private val onHandlerException: (
        listener: Any,
        method: Method,
        exception: Throwable
    ) -> Unit
) : PacketDispatcher {

    override fun dispatch(event: PacketEvent<out PacketView>) {
        for (handler in registry.snapshotHandlers()) {
            if (!handler.eventType.isInstance(event)) {
                continue
            }

            if (handler.ignoreCanceled && event.isCanceled) {
                continue
            }

            try {
                handler.method.invoke(handler.listener, event)
            } catch (exception: InvocationTargetException) {
                reportException(handler, exception.targetException)
            } catch (exception: ReflectiveOperationException) {
                reportException(handler, exception)
            } catch (exception: RuntimeException) {
                reportException(handler, exception)
            }
        }
    }

    private fun reportException(
        handler: PacketHandlerMethod,
        exception: Throwable
    ) {
        onHandlerException(
            handler.listener,
            handler.method,
            exception
        )
    }

}