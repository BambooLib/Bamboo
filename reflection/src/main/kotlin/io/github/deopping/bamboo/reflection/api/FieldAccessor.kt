package io.github.deopping.bamboo.reflection.api

/**
 * Provides prepared access to a field.
 *
 * Supports reading and writing instance and static fields without
 * repeatedly performing reflective field lookup and access preparation.
 *
 * @author DeOpping
 * @since 0.1.0
 */
interface FieldAccessor {

    /**
     * Reads the field value from the supplied instance.
     *
     * @param instance the instance containing the field
     * @return the current field value, or `null` if the field contains `null`
     * @throws IllegalArgumentException if this accessor represents a static field
     * @see getStatic
     * @since 0.1.0
     */
    fun get(instance: Any): Any?

    /**
     * Updates the field value on the supplied instance.
     *
     * @param instance the instance containing the field
     * @param value the value to assign to the field
     * @throws IllegalArgumentException if this field accessor represents a static field
     * or the value is incompatible with the field type
     * @throws IllegalStateException if the field cannot be written
     * @since 0.1.0
     */
    fun set(instance: Any, value: Any?)

    /**
     * Reads the value of a static field.
     *
     * @return the current field value, or `null` if the field contains `null`
     * @throws IllegalArgumentException if this accessor represents an instance field
     * @see get
     * @since 0.1.0
     */
    fun getStatic(): Any?

    /**
     * Updates the value of a static field.
     *
     * @param value the value to assign the field to
     * @throws IllegalArgumentException if this accessor represents an instance field
     * or the value is incompatible with the field type
     * @throws IllegalStateException if the field cannot be written
     * @see set
     * @since 0.1.0
     */
    fun setStatic(value: Any?)

}