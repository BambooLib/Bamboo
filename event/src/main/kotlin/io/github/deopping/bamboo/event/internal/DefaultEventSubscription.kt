package io.github.deopping.bamboo.event.internal

import io.github.deopping.bamboo.event.api.EventSubscription

internal class DefaultEventSubscription(
    private val unregisterAction: (DefaultEventSubscription) -> Boolean,
) : EventSubscription {
    @Volatile
    private var registered: Boolean = true

    public override val isRegistered: Boolean
        get() = registered

    public override fun unregister(): Boolean = unregisterAction(this)

    public override fun close() {
        unregister()
    }

    internal fun markUnregistered() {
        registered = false
    }
}
