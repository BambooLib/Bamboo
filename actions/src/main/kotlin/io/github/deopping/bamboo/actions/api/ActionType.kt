package io.github.deopping.bamboo.actions.api

import io.github.deopping.bamboo.common.api.NamespacedKey

/**
 * Identifies a registered action type.
 *
 * Action types use a namespaced key to prevent registration conflicts
 * between Bamboo and third-party plugins.
 *
 * @property namespacedKey the namespaced key owning this action type
 * @author DeOpping
 * @since 0.1.0
 */
data class ActionType(
    val namespacedKey: NamespacedKey
) {

    /**
     * Creates an action type from a namespace and name.
     *
     * @param namespace the namespace owning this action type
     * @param name the name of this action type
     * @since 0.1.0
     */
    constructor(
        namespace: String,
        name: String
    ) : this(NamespacedKey(namespace, name))

    /**
     * Returns the namespaced identifier of this action type.
     * @since 0.1.0
     */
    override fun toString(): String = namespacedKey.toString()

}
