package io.github.deopping.bamboo.geometry.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs

private class RotationTest {
    @Test
    fun `converts cardinal rotations into Minecraft directions`() {
        assertVectorEquals(Vector3(0.0, 0.0, 1.0), Rotation(0.0, 0.0).toDirection())
        assertVectorEquals(Vector3(-1.0, 0.0, 0.0), Rotation(90.0, 0.0).toDirection())
        assertVectorEquals(Vector3(1.0, 0.0, 0.0), Rotation(-90.0, 0.0).toDirection())
        assertVectorEquals(Vector3(0.0, 0.0, -1.0), Rotation(180.0, 0.0).toDirection())
        assertVectorEquals(Vector3(0.0, -1.0, 0.0), Rotation(0.0, 90.0).toDirection())
        assertVectorEquals(Vector3(0.0, 1.0, 0.0), Rotation(0.0, -90.0).toDirection())
    }

    @Test
    fun `direction vectors have unit length`() {
        val direction = Rotation(137.5, 24.5).toDirection()

        assertEquals(1.0, direction.length(), GEOMETRY_EPSILON)
    }

    @Test
    fun `creates rotations from direction vectors`() {
        assertRotationEquals(
            Rotation(90.0, 0.0),
            Rotation.fromDirection(Vector3(-1.0, 0.0, 0.0)),
        )
        assertRotationEquals(
            Rotation(0.0, 0.0),
            Rotation.fromDirection(Vector3(0.0, 0.0, 10.0)),
        )
    }

    @Test
    fun `round trips rotations through direction vectors`() {
        val original = Rotation(137.5, -24.5)
        val reconstructed = Rotation.fromDirection(original.toDirection())

        assertRotationEquals(original, reconstructed)
    }

    @Test
    fun `normalizes yaw into the expected range`() {
        assertEquals(90.0, RotationLike.normalizeDegrees(450.0), GEOMETRY_EPSILON)
        assertEquals(-90.0, RotationLike.normalizeDegrees(270.0), GEOMETRY_EPSILON)
        assertEquals(-180.0, RotationLike.normalizeDegrees(180.0), GEOMETRY_EPSILON)
        assertEquals(-180.0, RotationLike.normalizeDegrees(-540.0), GEOMETRY_EPSILON)
    }

    @Test
    fun `normalized rotation preserves pitch`() {
        val rotation = Rotation(450.0, 30.0).normalized()

        assertEquals(90.0, rotation.yaw, GEOMETRY_EPSILON)
        assertEquals(30.0, rotation.pitch, GEOMETRY_EPSILON)
    }

    @Test
    fun `interpolates yaw using the shortest path`() {
        val first = Rotation(170.0, 0.0)
        val second = Rotation(-170.0, 20.0)

        val midpoint = first.lerp(second, 0.5)

        assertEquals(180.0, abs(midpoint.yaw), GEOMETRY_EPSILON)
        assertEquals(10.0, midpoint.pitch, GEOMETRY_EPSILON)
    }

    @Test
    fun `rejects interpolation progress outside its range`() {
        val rotation = Rotation(0.0, 0.0)

        assertThrows<IllegalArgumentException> {
            rotation.lerp(Rotation(90.0, 30.0), -0.1)
        }

        assertThrows<IllegalArgumentException> {
            rotation.lerp(Rotation(90.0, 30.0), 1.1)
        }
    }

    @Test
    fun `rejects invalid pitch`() {
        assertThrows<IllegalArgumentException> {
            Rotation(0.0, 90.001)
        }

        assertThrows<IllegalArgumentException> {
            Rotation(0.0, -90.001)
        }
    }

    @Test
    fun `rejects non-finite components`() {
        assertThrows<IllegalArgumentException> {
            Rotation(Double.NaN, 0.0)
        }

        assertThrows<IllegalArgumentException> {
            Rotation(0.0, Double.POSITIVE_INFINITY)
        }
    }

    @Test
    fun `rejects a zero direction vector`() {
        assertThrows<IllegalArgumentException> {
            Rotation.fromDirection(Vector3(0.0, 0.0, 0.0))
        }
    }

    @Test
    fun `has immutable value semantics`() {
        val first = Rotation(90.0, 30.0)
        val second = Rotation(90.0, 30.0)

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertEquals(first, first.copy())
    }
}
