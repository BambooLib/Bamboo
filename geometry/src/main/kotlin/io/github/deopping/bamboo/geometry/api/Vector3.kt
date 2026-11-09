package io.github.deopping.bamboo.geometry.api

/**
 * Represents an immutable three-dimensional vector.
 *
 * @property x the vector's X component
 * @property y the vector's Y component
 * @property z the vector's Z component
 * @author DeOpping
 * @since 1.0.0
 */
public data class Vector3(
    override val x: Double,
    override val y: Double,
    override val z: Double,
) : Vector3Like {
    init {
        require(x.isFinite()) {
            "X component must be finite."
        }

        require(y.isFinite()) {
            "Y component must be finite."
        }

        require(z.isFinite()) {
            "Z component must be finite."
        }
    }

    /**
     * Converts this vector into a mutable vector.
     *
     * @return a new mutable vector with the same components
     * @since 1.0.0
     */
    public fun toMutable(): MutableVector3 = MutableVector3(x, y, z)

    /**
     * Contains constants and factory methods for immutable vectors.
     *
     * @author DeOpping
     * @since 1.0.0
     */
    public companion object {
        /**
         * Represents a vector with all components set to zero.
         *
         * @since 1.0.0
         */
        public val ZERO: Vector3 = Vector3(0.0, 0.0, 0.0)
    }
}
