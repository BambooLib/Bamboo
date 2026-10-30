package io.github.deopping.bamboo.plugin.internal

import io.github.deopping.bamboo.plugin.api.BambooPlugin
import io.github.deopping.bamboo.plugin.api.PluginContext
import io.github.deopping.bamboo.plugin.api.PluginRuntime
import io.github.deopping.bamboo.plugin.api.PluginState
import java.util.Optional

internal class DefaultPluginRuntime(
    override val plugin: BambooPlugin,
    override val context: PluginContext,
) : PluginRuntime {
    private var currentState: PluginState = PluginState.UNLOADED

    private var currentFailure: Optional<Throwable> = Optional.empty()

    override val state: PluginState
        get() = currentState

    override val failure: Optional<Throwable>
        get() = currentFailure

    override fun load() {
        transition(
            expected = PluginState.UNLOADED,
            transitioning = PluginState.LOADING,
            completed = PluginState.LOADED,
        ) {
            plugin.onLoad(context)
        }
    }

    override fun enable() {
        transition(
            expected = PluginState.LOADED,
            transitioning = PluginState.ENABLING,
            completed = PluginState.ENABLED,
        ) {
            plugin.onEnable(context)
        }
    }

    override fun disable() {
        transition(
            expected = PluginState.ENABLED,
            transitioning = PluginState.DISABLING,
            completed = PluginState.DISABLED,
        ) {
            plugin.onDisable(context)
        }
    }

    private fun transition(
        expected: PluginState,
        transitioning: PluginState,
        completed: PluginState,
        operation: () -> Unit,
    ) {
        check(currentState == expected) {
            "Cannot transition from $currentState to $transitioning."
        }

        currentState = transitioning

        try {
            operation()
            currentState = completed
        } catch (failure: Throwable) {
            currentFailure = Optional.of(failure)
            currentState = PluginState.FAILED

            throw failure
        }
    }
}
