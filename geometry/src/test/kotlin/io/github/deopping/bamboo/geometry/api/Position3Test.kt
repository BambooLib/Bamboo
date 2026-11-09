package io.github.deopping.bamboo.geometry.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.sqrt

private class Position3Test {
    @Test
    fun `calculates distances`() {
        val first = Position3(1.0, 2.0, 3.0)
        val second = Position3(4.0, 6.0, 15.0)

        assertEquals(169.0, first.distanceSquaredTo(second), GEOMETRY_EPSILON)
        assertEquals(13.0, first.distanceTo(second), GEOMETRY_EPSILON)
    }

    @Test
    fun `calculates distance without overflowing intermediate squares`() {
        val first = Position3(0.0, 0.0, 0.0)
        val second = Position3(1.0e200, 1.0e200, 0.0)

        assertEquals(
            1.0e200 * sqrt(2.0),
            first.distanceTo(second),
            1.0e190,
        )
    }

    @Test
    fun `calculates displacement toward another position`() {
        val first = Position3(1.0, 2.0, 3.0)
        val second = Position3(4.0, 6.0, 15.0)

        assertVectorEquals(Vector3(3.0, 4.0, 12.0), first.vectorTo(second))
    }

    @Test
    fun `supports position and vector arithmetic`() {
        val position = Position3(1.0, 2.0, 3.0)
        val vector = Vector3(4.0, 5.0, 6.0)

        assertPositionEquals(Position3(5.0, 7.0, 9.0), position + vector)
        assertPositionEquals(Position3(-3.0, -3.0, -3.0), position - vector)
        assertVectorEquals(Vector3(3.0, 3.0, 3.0), Position3(4.0, 5.0, 6.0) - position)
    }

    @Test
    fun `lerps and supports extrapolation`() {
        val first = Position3(1.0, 2.0, 3.0)
        val second = Position3(4.0, 5.0, 6.0)

        assertPositionEquals(Position3(2.5, 3.5, 4.5), first.lerp(second, 0.5))
        assertPositionEquals(Position3(7.0, 8.0, 9.0), first.lerp(second, 2.0))
    }

    @Test
    fun `identifies the origin`() {
        assertTrue(Position3(0.0, 0.0, 0.0).isOrigin)
        assertFalse(Position3(0.0, 0.0, 1.0).isOrigin)
    }

    @Test
    fun `rejects non-finite components`() {
        assertThrows<IllegalArgumentException> {
            Position3(Double.NaN, 0.0, 0.0)
        }

        assertThrows<IllegalArgumentException> {
            Position3(0.0, Double.POSITIVE_INFINITY, 0.0)
        }
    }

    @Test
    fun `has immutable value semantics`() {
        val first = Position3(1.0, 2.0, 3.0)
        val second = Position3(1.0, 2.0, 3.0)

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertEquals(first, first.copy())
    }
}
