package io.github.deopping.bamboo.reflection.api

/**
 * Provides prepared access to a field.
 *
 * Supports reading and writing instance and static fields without repeatedly
 * performing reflective field lookup and access preparation.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface FieldAccessor : ReflectionAccessor {
    /**
     * Indicates whether this accessor represents a static field.
     *
     * @return `true` if the field is static
     * @since 1.0.0
     */
    public val isStatic: Boolean

    /**
     * Indicates whether this field can be written through this accessor.
     *
     * Final fields are not writable.
     *
     * @return `true` if the field is writable
     * @since 1.0.0
     */
    public val isWritable: Boolean

    /**
     * Reads the field value from the supplied instance.
     *
     * @param instance the instance containing the field
     * @return the current field value
     * @throws IllegalArgumentException if the accessor represents a static field
     * @since 1.0.0
     */
    public fun get(instance: Any): Any?

    /**
     * Updates the field value on the supplied instance.
     *
     * @param instance the instance containing the field
     * @param value the value to assign to the field
     * @throws IllegalArgumentException if this accessor represents a static field
     * or the value is incompatible with the field type
     * @throws IllegalStateException if the field cannot be written
     * @since 1.0.0
     */
    public fun set(
        instance: Any,
        value: Any?,
    )

    /**
     * Reads the value of a static field.
     *
     * @return the current field value
     * @throws IllegalArgumentException if this accessor represents an instance field
     * @since 1.0.0
     */
    public fun getStatic(): Any?

    /**
     * Updates the value of a static field.
     *
     * @param value the value to assign to the field
     * @throws IllegalArgumentException if this accessor represents an instance field
     * or the value is incompatible with the field type
     * @throws IllegalStateException if the field cannot be written
     * @since 1.0.0
     */
    public fun setStatic(value: Any?)
}
