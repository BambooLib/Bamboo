package io.github.deopping.bamboo.actions.api

/**
 * Represents a platform-independent action.
 *
 * An action performs an operation using a platform-provided execution
 * context without depending on a particular server implementation.
 *
 * @param C the type of execution context required by this action
 * @author DeOpping
 * @since 0.1.0
 */
fun interface Action<in C> {

    /**
     * Executes this action using the provided context.
     *
     * @param context the context in which this action is executed
     * @since 0.1.0
     */
    fun execute(context: C)

}