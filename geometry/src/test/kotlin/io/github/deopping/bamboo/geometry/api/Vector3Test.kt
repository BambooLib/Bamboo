package io.github.deopping.bamboo.geometry.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.sqrt

private class Vector3Test {
    @Test
    fun `calculates vector lengths`() {
        val vector = Vector3(1.0, 2.0, 3.0)

        assertEquals(14.0, vector.lengthSquared(), GEOMETRY_EPSILON)
        assertEquals(sqrt(14.0), vector.length(), GEOMETRY_EPSILON)
    }

    @Test
    fun `calculates dot and cross products`() {
        val first = Vector3(1.0, 2.0, 3.0)
        val second = Vector3(4.0, -5.0, 6.0)

        assertEquals(12.0, first.dot(second), GEOMETRY_EPSILON)
        assertVectorEquals(Vector3(27.0, 6.0, -13.0), first.cross(second))
    }

    @Test
    fun `supports vector arithmetic`() {
        val first = Vector3(1.0, 2.0, 3.0)
        val second = Vector3(4.0, -5.0, 6.0)

        assertVectorEquals(Vector3(5.0, -3.0, 9.0), first + second)
        assertVectorEquals(Vector3(-3.0, 7.0, -3.0), first - second)
        assertVectorEquals(Vector3(2.0, 4.0, 6.0), first * 2.0)
        assertVectorEquals(Vector3(0.5, 1.0, 1.5), first / 2.0)
        assertVectorEquals(Vector3(-1.0, -2.0, -3.0), -first)
    }

    @Test
    fun `normalizes vectors`() {
        val normalized = Vector3(1.0, 2.0, 3.0).normalized()

        assertEquals(1.0, normalized.length(), GEOMETRY_EPSILON)
    }

    @Test
    fun `normalizes vectors without overflowing intermediate calculations`() {
        val huge = Vector3(1.0e308, 1.0e308, 0.0).normalized()
        val tiny = Vector3(Double.MIN_VALUE, 0.0, 0.0).normalized()

        assertEquals(1.0, huge.length(), GEOMETRY_EPSILON)
        assertEquals(sqrt(0.5), huge.x, GEOMETRY_EPSILON)
        assertEquals(sqrt(0.5), huge.y, GEOMETRY_EPSILON)

        assertVectorEquals(Vector3(1.0, 0.0, 0.0), tiny)
    }

    @Test
    fun `rejects normalized of a zero vector`() {
        assertThrows<IllegalArgumentException> {
            Vector3(0.0, 0.0, 0.0).normalized()
        }
    }

    @Test
    fun `lerps and supports extrapolation`() {
        val first = Vector3(1.0, 2.0, 3.0)
        val second = Vector3(4.0, 5.0, 6.0)

        assertVectorEquals(Vector3(2.5, 3.5, 4.5), first.lerp(second, 0.5))
        assertVectorEquals(Vector3(7.0, 8.0, 9.0), first.lerp(second, 2.0))
    }

    @Test
    fun `rejects non-finite components`() {
        assertThrows<IllegalArgumentException> {
            Vector3(Double.NaN, 0.0, 0.0)
        }

        assertThrows<IllegalArgumentException> {
            Vector3(0.0, Double.POSITIVE_INFINITY, 0.0)
        }

        assertThrows<IllegalArgumentException> {
            Vector3(0.0, 0.0, Double.NEGATIVE_INFINITY)
        }
    }

    @Test
    fun `rejects non-finite interpolation progress`() {
        val vector = Vector3(1.0, 2.0, 3.0)

        assertThrows<IllegalArgumentException> {
            vector.lerp(Vector3(4.0, 5.0, 6.0), Double.NaN)
        }
    }

    @Test
    fun `identifies zero vectors`() {
        assertTrue(Vector3(0.0, 0.0, 0.0).isZero)
        assertFalse(Vector3(0.0, Double.MIN_VALUE, 0.0).isZero)
    }

    @Test
    fun `has immutable value semantics`() {
        val first = Vector3(1.0, 2.0, 3.0)
        val second = Vector3(1.0, 2.0, 3.0)

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertEquals(first, first.copy())
    }
}
