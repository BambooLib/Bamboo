package io.github.deopping.bamboo.reflection.api

/**
 * Base interface for prepared reflection accessors.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface ReflectionAccessor {
    /**
     * Removes this accessor from its owning reflection API's cache.
     *
     * This accessor remains usable after being unregistered. Subsequent requests
     * for the same reflective member may create a new accessor.
     *
     * @return `true` if this exact accessor was removed from the cache, or `false`
     * if it was not currently cached
     * @since 1.0.0
     */
    public fun unregister(): Boolean
}
