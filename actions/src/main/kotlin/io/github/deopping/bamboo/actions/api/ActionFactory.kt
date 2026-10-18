package io.github.deopping.bamboo.actions.api

/**
 * Creates configured actions of a registered type.
 *
 * Implementations are responsible for interpreting the supplied
 * configuration and constructing the corresponding action.
 *
 * @param C the execution context accepted by the created actions
 * @param A the configuration type accepted by this factory
 * @author DeOpping
 * @since 0.1.0
 */
fun interface ActionFactory<C, in A : ActionConfig> {

    /**
     * Creates an action from the supplied configuration.
     *
     * @param configuration the configuration for this action
     * @return the configured action
     * @since 0.1.0
     */
    fun create(configuration: A): Action<C>

}