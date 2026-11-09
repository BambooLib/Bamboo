package io.github.deopping.bamboo.geometry.api

import io.github.deopping.bamboo.geometry.internal.GeometryMath

/**
 * Represents a mutable position in three-dimensional space.
 *
 * Mutable positions use reference equality and are not thread-safe.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public class MutablePosition3(
    x: Double,
    y: Double,
    z: Double,
) : Position3Like {
    private var xCoordinate: Double = x
    private var yCoordinate: Double = y
    private var zCoordinate: Double = z

    /**
     * Gets or sets the X coordinate.
     * @since 1.0.0
     */
    public override var x: Double
        get() = xCoordinate
        set(value) {
            GeometryMath.requireFinite(value, "X coordinate")
            xCoordinate = value
        }

    /**
     * Gets or sets the Y coordinate.
     * @since 1.0.0
     */
    public override var y: Double
        get() = yCoordinate
        set(value) {
            GeometryMath.requireFinite(value, "Y coordinate")
            yCoordinate = value
        }

    /**
     * Gets or sets the Z coordinate.
     * @since 1.0.0
     */
    public override var z: Double
        get() = zCoordinate
        set(value) {
            GeometryMath.requireFinite(value, "Z coordinate")
            zCoordinate = value
        }

    init {
        GeometryMath.requireFinite3(x, y, z, "coordinate")
    }

    /**
     * Replaces all position coordinates.
     *
     * All coordinates are validated before any modification occurs.
     *
     * @param x the new X coordinate
     * @param y the new Y coordinate
     * @param z the new Z coordinate
     * @return this immutable position
     * @since 1.0.0
     */
    public fun set(
        x: Double,
        y: Double,
        z: Double,
    ): MutablePosition3 {
        GeometryMath.requireFinite3(x, y, z, "coordinate")

        xCoordinate = x
        yCoordinate = y
        zCoordinate = z

        return this
    }

    /**
     * Copies the coordinates from another position.
     *
     * @param other the position to copy
     * @return this mutable position
     * @since 1.0.0
     */
    public fun set(other: Position3Like): MutablePosition3 = set(other.x, other.y, other.z)

    /**
     * Moves this position by a displacement in place.
     *
     * @param vector the displacement
     * @since 1.0.0
     */
    public operator fun plusAssign(vector: Vector3Like) {
        set(
            x + vector.x,
            y + vector.y,
            z + vector.z,
        )
    }

    /**
     * Moves this position by the inverse of a displacement in place.
     *
     * @param vector the displacement
     * @since 1.0.0
     */
    public operator fun minusAssign(vector: Vector3Like) {
        set(
            x - vector.x,
            y - vector.y,
            z - vector.z,
        )
    }

    /**
     * Moves this position to the midpoint between this position and
     * the other position.
     *
     * @param other the other position
     * @return this mutable position
     * @since 1.0.0
     */
    public fun midpointAssign(other: Position3Like): MutablePosition3 =
        set(
            x * 0.5 + other.x * 0.5,
            y * 0.5 + other.y * 0.5,
            z * 0.5 + other.z * 0.5,
        )

    /**
     * Interpolates this position toward another position in place.
     *
     * @param other the target position
     * @param progress the interpolation progress
     * @return this mutable position
     * @throws IllegalArgumentException if progress is not finite
     * @since 1.0.0
     */
    public fun lerpAssign(
        other: Position3Like,
        progress: Double,
    ): MutablePosition3 {
        GeometryMath.requireFinite(progress, "Progress")

        if (progress == 0.0) {
            return this
        } else if (progress == 1.0) {
            return set(other)
        }

        return set(
            x + (other.x - x) * progress,
            y + (other.y - y) * progress,
            z + (other.z - z) * progress,
        )
    }

    /**
     * Converts this position into an immutable position.
     *
     * The returned position is an independent snapshot.
     *
     * @return a new immutable position
     * @since 1.0.0
     */
    public fun toImmutable(): Position3 = Position3(x, y, z)
}
