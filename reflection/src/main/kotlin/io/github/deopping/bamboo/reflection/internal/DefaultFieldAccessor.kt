package io.github.deopping.bamboo.reflection.internal

import io.github.deopping.bamboo.reflection.api.FieldAccessor
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodType
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.Optional

internal class DefaultFieldAccessor(
    field: Field,
    getter: MethodHandle,
    setter: MethodHandle?,
    private val unregister: (FieldAccessor) -> Boolean,
) : FieldAccessor {
    private val fieldName: String = "${field.declaringClass.name}.${field.name}"

    override val isStatic: Boolean = Modifier.isStatic(field.modifiers)

    private val getterHandle: MethodHandle =
        if (isStatic) {
            getter.asType(
                MethodType.methodType(
                    Any::class.java,
                ),
            )
        } else {
            getter.asType(
                MethodType.methodType(
                    Any::class.java,
                    Any::class.java,
                ),
            )
        }

    private val setterHandle: Optional<MethodHandle> =
        Optional.ofNullable(
            setter?.asType(
                if (isStatic) {
                    MethodType.methodType(
                        Void.TYPE,
                        Any::class.java,
                    )
                } else {
                    MethodType.methodType(
                        Void.TYPE,
                        Any::class.java,
                        Any::class.java,
                    )
                },
            ),
        )

    override val isWritable: Boolean = setterHandle.isPresent

    override fun get(instance: Any): Any? {
        require(!isStatic) {
            "Cannot read a static field using get(). Use getStatic() instead."
        }

        return getterHandle.invokeExact(instance)
    }

    override fun set(
        instance: Any,
        value: Any?,
    ) {
        require(!isStatic) {
            "Cannot write a static field using set(). Use setStatic() instead."
        }

        val handle =
            setterHandle.orElseThrow {
                IllegalStateException(
                    "Cannot write field '$fieldName' because it is not writable.",
                )
            }

        handle.invokeExact(instance, value)
    }

    override fun getStatic(): Any? {
        require(isStatic) {
            "Cannot read an instance field using getStatic(). Use get() instead."
        }

        return getterHandle.invokeExact()
    }

    override fun setStatic(value: Any?) {
        require(isStatic) {
            "Cannot write an instance field using setStatic(). Use set() instead."
        }

        val handle =
            setterHandle.orElseThrow {
                IllegalStateException(
                    "Cannot write field '$fieldName' because it is not writable.",
                )
            }

        handle.invokeExact(value)
    }

    override fun unregister(): Boolean = unregister(this)
}
