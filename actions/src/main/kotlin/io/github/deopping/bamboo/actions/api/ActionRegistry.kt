package io.github.deopping.bamboo.actions.api

import kotlin.reflect.KClass

/**
 * Manages action type registrations and creates configured actions.
 *
 * @param C the execution context accepted by registered actions
 * @author DeOpping
 * @since 0.1.0
 */
interface ActionRegistry<C> {

    /**
     * Returns a snapshot of currently registered action types.
     * @since 0.1.0
     */
    val registeredTypes: Set<ActionType>

    /**
     * Registers a factory for an action type.
     *
     * @param type the factory creating configured instances
     * @return a handle that can remove the registration
     * @throws ActionException if the type is already registered
     * @since 0.1.0
     */
    fun <A : ActionConfig> register(
        type: ActionType,
        configType: KClass<A>,
        factory: ActionFactory<C, A>
    ): ActionRegistration

    /**
     * Creates a configured action using its registered factory
     *
     * @param type the requested action type
     * @param configuration the configuration passed to its factory
     * @return the configured action
     * @throws ActionException if the type is unknown or creation fails
     * @since 0.1.0
     * @see Config
     */
    fun <A : ActionConfig> create(
        type: ActionType,
        configuration: A
    ): Action<C>

    /**
     * Checks whether an action type is registered.
     *
     * @param type the action type to check
     * @return `true` if the type is registered, `false` otherwise
     * @since 0.1.0
     */
    fun contains(type: ActionType): Boolean

}