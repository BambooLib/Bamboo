package io.github.deopping.bamboo.geometry.api

/**
 * Provides read-only access to three-dimensional coordinates.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface Coordinate3Like {
    /**
     * Gets the X coordinate.
     * @since 1.0.0
     */
    public val x: Double

    /**
     * Gets the Y coordinate.
     * @since 1.0.0
     */
    public val y: Double

    /**
     * Gets the Z coordinate.
     * @since 1.0.0
     */
    public val z: Double
}
