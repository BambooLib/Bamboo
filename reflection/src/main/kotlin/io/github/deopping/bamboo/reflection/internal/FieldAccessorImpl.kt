package io.github.deopping.bamboo.reflection.internal

import io.github.deopping.bamboo.reflection.api.FieldAccessor
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodType
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * @author DeOpping
 * @since 0.1.0
 */
internal class FieldAccessorImpl(
    field: Field,
    getter: MethodHandle,
    setter: MethodHandle?
) : FieldAccessor {

    private val fieldName = "${field.declaringClass.name}.${field.name}"
    private val isStatic = Modifier.isStatic(field.modifiers)
    private val getterHandle: MethodHandle
    private val setterHandle: MethodHandle?

    init {
        getterHandle = if (isStatic) {
            getter.asType(MethodType.methodType(
                Any::class.java
            ))
        } else {
            getter.asType(MethodType.methodType(
                Any::class.java,
                Any::class.java
            ))
        }

        setterHandle = setter?.let { handle ->
            if (isStatic) {
                handle.asType(MethodType.methodType(
                    Any::class.java,
                    Any::class.java
                ))
            } else {
                handle.asType(MethodType.methodType(
                    Any::class.java,
                    Any::class.java,
                    Any::class.java
                ))
            }
        }
    }

    override fun get(instance: Any): Any? {
        require(!isStatic) {
            "Cannot read a static field type using get(). Use getStatic() instead."
        }

        return getterHandle.invokeExact(instance)
    }

    override fun set(instance: Any, value: Any?) {
        require(!isStatic) {
            "Cannot write a static field using set(). Use staticSet() instead."
        }

        val handle = checkNotNull(setterHandle) {
            "Cannot write field '$fieldName' because it is not writable."
        }

        val ignored: Any? = handle.invokeExact(instance, value)
    }

    override fun getStatic(): Any? {
        require(isStatic) {
            "Cannot read an instance field using getStatic(). Use get() instead."
        }

        return getterHandle.invokeExact()
    }

    override fun setStatic(value: Any?) {
        require(isStatic) {
            "Cannot write an instance field using getStatic(). Use set() instead."
        }

        val handle = checkNotNull(setterHandle) {
            "Cannot write field '$fieldName' because it is not writable."
        }

        val ignored: Any? = handle.invokeExact(value)
    }


}