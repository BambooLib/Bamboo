package io.github.deopping.bamboo.geometry.api

/**
 * Represents a mutable block position in three-dimensional integer space.
 *
 * Mutating this position does not create a new position instance.
 *
 * Mutable block positions use reference equality and are not thread-safe.
 * External synchronization is required when sharing instances between threads.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public class MutableBlockPosition(
    x: Int,
    y: Int,
    z: Int,
) : BlockPositionLike {
    /**
     * Gets or sets the X block coordinate.
     * @since 1.0.0
     */
    public override var x: Int = x

    /**
     * Gets or sets the Y block coordinate.
     * @since 1.0.0
     */
    public override var y: Int = y

    /**
     * Gets or sets the Z block coordinate.
     * @since 1.0.0
     */
    public override var z: Int = z

    /**
     * Replaces all block coordinates.
     *
     * @param x the new X coordinate
     * @param y the new Y coordinate
     * @param z the new Z coordinate
     * @return this mutable block position
     * @since 1.0.0
     */
    public fun set(
        x: Int,
        y: Int,
        z: Int,
    ): MutableBlockPosition {
        this.x = x
        this.y = y
        this.z = z

        return this
    }

    /**
     * Copies the coordinates from another block position.
     *
     * @param other the block position to copy
     * @return this mutable block position
     * @since 1.0.0
     */
    public fun set(other: BlockPositionLike): MutableBlockPosition = set(other.x, other.y, other.z)

    /**
     * Moves this block position by an integer offset in place.
     *
     * All resulting coordinates are calculated before any coordinate is
     * modified. If an operation overflows, this position remains unchanged.
     *
     * @param dx the X offset
     * @param dy the Y offset
     * @param dz the Z offset
     * @return this mutable block position
     * @throws ArithmeticException if any resulting coordinate overflows
     * @since 1.0.0
     */
    public fun offsetAssign(
        dx: Int,
        dy: Int,
        dz: Int,
    ): MutableBlockPosition {
        val newX = Math.addExact(x, dx)
        val newY = Math.addExact(y, dy)
        val newZ = Math.addExact(z, dz)

        return set(newX, newY, newZ)
    }

    /**
     * Moves this block position by another block position's coordinates in place.
     *
     * @param offset the integer offset
     * @return this mutable block position
     * @throws ArithmeticException if any resulting coordinate overflows
     * @since 1.0.0
     */
    public fun offsetAssign(offset: BlockPositionLike): MutableBlockPosition =
        offsetAssign(offset.x, offset.y, offset.z)

    /**
     * Converts this block position into an immutable block position.
     *
     * The returned position is an independent snapshot.
     *
     * @return a new immutable block position
     * @since 1.0.0
     */
    public fun toImmutable(): BlockPosition = BlockPosition(x, y, z)
}
