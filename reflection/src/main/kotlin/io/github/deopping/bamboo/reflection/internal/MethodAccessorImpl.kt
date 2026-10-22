package io.github.deopping.bamboo.reflection.internal

import io.github.deopping.bamboo.reflection.api.MethodAccessor
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodType

/**
 * @author DeOpping
 * @since 0.1.0
 */
internal class MethodAccessorImpl(
    handle: MethodHandle,
    private val isStatic: Boolean
) : MethodAccessor {

    private val invocationHandle: MethodHandle

    init {
        val parameterCount = handle.type().parameterCount()

        invocationHandle = if (isStatic) {
            handle
                .asSpreader(Array<Any?>::class.java, parameterCount)
                .asType(MethodType.methodType(
                    Any::class.java,
                    Array<Any?>::class.java
                ))
        } else {
            handle
                .asSpreader(
                    1,
                    Array<Any?>::class.java,
                    parameterCount - 1
                )
                .asType(MethodType.methodType(
                    Any::class.java,
                    Any::class.java,
                    Array<Any?>::class.java
                ))
        }
    }

    override fun invoke(instance: Any, vararg arguments: Any?): Any? {
        require(!isStatic) {
            "Cannot invoke a static method using invoke(). Use invokeStatic() instead."
        }

        return invocationHandle.invokeExact(instance, arguments)
    }

    override fun invokeStatic(vararg arguments: Any?): Any? {
        require(isStatic) {
            "Cannot invoke an instance method using invokeStatic(). Use invoke() instead."
        }

        return invocationHandle.invokeExact(arguments)
    }

}