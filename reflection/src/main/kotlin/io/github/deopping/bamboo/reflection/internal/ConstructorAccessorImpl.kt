package io.github.deopping.bamboo.reflection.internal

import io.github.deopping.bamboo.reflection.api.ConstructorAccessor
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodType

/**
 * @author DeOpping
 * @since 0.1.0
 */
internal class ConstructorAccessorImpl(
    handle: MethodHandle
) : ConstructorAccessor<Any> {

    private val invocationHandle: MethodHandle

    init {
        val parameterCount = handle.type().parameterCount()

        invocationHandle = handle
            .asSpreader(Array<Any?>::class.java, parameterCount)
            .asType(
                MethodType.methodType(
                    Any::class.java,
                    Array<Any?>::class.java
                )
            )
    }

    override fun newInstance(vararg arguments: Any?): Any {
        return invocationHandle.invokeExact(arguments)
    }

}