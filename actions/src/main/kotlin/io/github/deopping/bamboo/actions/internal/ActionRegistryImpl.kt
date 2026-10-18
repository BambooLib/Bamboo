package io.github.deopping.bamboo.actions.internal

import io.github.deopping.bamboo.actions.api.*
import kotlin.reflect.KClass

internal class ActionRegistryImpl<C> : ActionRegistry<C> {

    private val lock = Any()

    private val registrations =
        LinkedHashMap<ActionType, RegisteredAction<C>>()

    override val registeredTypes: Set<ActionType>
        get() = synchronized(lock) {
            registrations.keys.toSet()
        }

    override fun <A : ActionConfig> register(
        type: ActionType,
        configType: KClass<A>,
        factory: ActionFactory<C, A>
    ): ActionRegistration {
        val registeredAction = RegisteredAction<C>(
            configType = configType,
            factory = { configuration ->
                if (!configType.isInstance(configuration)) {
                    throw ActionException(
                        "Action '$type' expects configuration " +
                        "'${configType.qualifiedName}', but received " +
                        "'${configuration::class.qualifiedName}'."
                    )
                }

                @Suppress("UNCHECKED_CAST")
                factory.create(configuration as A)
            }
        )

        synchronized(lock) {
            if (registrations.containsKey(type)) {
                throw ActionException(
                    "Action type '$type' is already registered."
                )
            }

            registrations[type] = registeredAction
        }

        return ActionRegistrationImpl(
            type = type,
            isActive = {
                synchronized(lock) {
                    registrations[type] === registeredAction
                }
            },
            onUnregister = {
                synchronized(lock) {
                    if (registrations[type] === registeredAction) {
                        registrations.remove(type)
                    }
                }
            }
        )
    }

    override fun <A : ActionConfig> create(
        type: ActionType,
        configuration: A
    ): Action<C> {
        val registeredAction = synchronized(lock) {
            registrations[type]
        } ?: throw ActionException(
            "Unknown action type '$type'."
        )

        return try {
            registeredAction.factory(configuration)
        } catch (exception: ActionException) {
            throw exception
        } catch (exception: Exception) {
            throw ActionException(
                "Failed to create action `$type`.",
                exception
            )
        }
    }

    override fun contains(type: ActionType): Boolean =
        synchronized(lock) {
            registrations.containsKey(type)
        }

    private class RegisteredAction<C>(
        val configType: KClass<out ActionConfig>,
        val factory: (ActionConfig) -> Action<C>
    )

}