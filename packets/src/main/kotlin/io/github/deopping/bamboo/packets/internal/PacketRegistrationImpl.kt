package io.github.deopping.bamboo.packets.internal

import io.github.deopping.bamboo.packets.api.PacketListener
import io.github.deopping.bamboo.packets.api.PacketRegistration

/**
 * @author DeOpping
 * @since 0.1.0
 */
internal class PacketRegistrationImpl(
    internal val listener: PacketListener,
    private val registry: PacketRegistryImpl
) : PacketRegistration {

    @Volatile
    private var registered = true

    override val isRegistered: Boolean
        get() = registered

    override fun unregister() {
        registry.unregister(this)
    }

    /**
     * Marks this registration as inactive.
     *
     * This method must only be called by the owning registry.
     * @since 0.1.0
     */
    internal fun deactivate() {
        registered = false
    }

}