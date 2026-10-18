package io.github.deopping.bamboo.actions

import io.github.deopping.bamboo.actions.api.ActionRegistry
import io.github.deopping.bamboo.actions.internal.ActionApiImpl

/**
 * Entry point for the Bamboo Actions module.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface ActionApi {

    /**
     * Creates an action registry for the supplied execution context type.
     *
     * @return a new, empty action registry
     * @since 0.1.0
     */
    fun <C> createRegistry(): ActionRegistry<C>

    companion object {
        /**
         * Creates an Action API instance.
         * @return a new Action API instance
         * @since 0.1.0
         */
        @JvmStatic
        fun create(): ActionApi = ActionApiImpl()
    }

}