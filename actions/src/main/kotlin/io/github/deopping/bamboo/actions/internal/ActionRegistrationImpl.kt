package io.github.deopping.bamboo.actions.internal

import io.github.deopping.bamboo.actions.api.ActionRegistration
import io.github.deopping.bamboo.actions.api.ActionType

internal class ActionRegistrationImpl(
    override val type: ActionType,
    private val isActive: () -> Boolean,
    private val onUnregister: () -> Unit
) : ActionRegistration {

    override val isRegistered: Boolean
        get() = isActive()

    override fun unregister() {
        onUnregister()
    }

}