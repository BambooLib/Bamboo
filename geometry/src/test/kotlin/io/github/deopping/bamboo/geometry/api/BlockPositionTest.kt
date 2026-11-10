package io.github.deopping.bamboo.geometry.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private class BlockPositionTest {
    @Test
    fun `calculates distance and squared distance`() {
        val first = BlockPosition(1, 2, 3)
        val second = BlockPosition(4, 6, 15)

        assertEquals(13.0, first.distanceTo(second), GEOMETRY_EPSILON)
        assertEquals(169.0, first.distanceSquaredTo(second), GEOMETRY_EPSILON)
    }

    @Test
    fun `calculates distance without integer overflow`() {
        val first = BlockPosition(Int.MIN_VALUE, 0, 0)
        val second = BlockPosition(Int.MAX_VALUE, 0, 0)
        val difference = Int.MAX_VALUE.toDouble() - Int.MIN_VALUE.toDouble()

        assertEquals(difference, first.distanceTo(second), 0.0)
    }

    @Test
    fun `calculates squared distance without integer overflow`() {
        val first = BlockPosition(Int.MIN_VALUE, 0, 0)
        val second = BlockPosition(Int.MAX_VALUE, 0, 0)
        val difference = Int.MAX_VALUE.toDouble() - Int.MIN_VALUE.toDouble()

        assertEquals(difference * difference, first.distanceSquaredTo(second), 0.0)
    }

    @Test
    fun `calculates midpoint with fractional coordinates`() {
        val first = BlockPosition(Int.MIN_VALUE, 10, -5)
        val second = BlockPosition(Int.MAX_VALUE, 11, 4)

        assertEquals(Position3(-0.5, 10.5, -0.5), first.midpoint(second))
    }

    @Test
    fun `calculates displacement toward another position`() {
        val first = BlockPosition(1, 2, 3)
        val second = BlockPosition(4, 6, 15)

        assertVectorEquals(Vector3(3.0, 4.0, 12.0), first.vectorTo(second))
        assertVectorEquals(Vector3(-3.0, -4.0, -12.0), first - second)
    }

    @Test
    fun `converts to continuous position and block center`() {
        val position = BlockPosition(1, -2, 3)

        assertEquals(Position3(1.0, -2.0, 3.0), position.toPosition())
        assertEquals(Position3(1.5, -1.5, 3.5), position.center())
    }

    @Test
    fun `identifies origin`() {
        assertTrue(BlockPosition.ORIGIN.isOrigin)
        assertFalse(BlockPosition(1, 0, 0).isOrigin)
        assertFalse(BlockPosition(0, 0, -1).isOrigin)
    }

    @Test
    fun `creates immutable offset without modifying original`() {
        val position = BlockPosition(10, 20, 30)

        val offset = position.offset(1, -2, 3)

        assertEquals(BlockPosition(10, 20, 30), position)
        assertEquals(BlockPosition(11, 18, 33), offset)
    }

    @Test
    fun `rejects offset that overflows`() {
        val position = BlockPosition(Int.MAX_VALUE, 0, 0)

        assertThrows<ArithmeticException> {
            position.offset(1, 0, 0)
        }

        assertEquals(BlockPosition(Int.MAX_VALUE, 0, 0), position)
    }

    @Test
    fun `creates block position from continuous position using floor`() {
        assertEquals(
            BlockPosition(3, -1, -2),
            BlockPosition.fromPosition(Position3(3.9, -0.1, -1.9)),
        )
    }

    @Test
    fun `converts coordinate at integer boundaries`() {
        assertEquals(
            BlockPosition(Int.MIN_VALUE, Int.MAX_VALUE, Int.MAX_VALUE),
            BlockPosition.fromPosition(
                Position3(
                    Int.MIN_VALUE.toDouble(),
                    Int.MAX_VALUE.toDouble(),
                    Int.MAX_VALUE.toDouble() + 0.75,
                ),
            ),
        )
    }

    @Test
    fun `rejects coordinates outside integer range`() {
        assertThrows<IllegalArgumentException> {
            BlockPosition.fromPosition(
                Position3(Int.MAX_VALUE.toDouble() + 1.0, 0.0, 0.0),
            )
        }

        assertThrows<IllegalArgumentException> {
            BlockPosition.fromPosition(
                Position3(Int.MIN_VALUE.toDouble() - 0.1, 0.0, 0.0),
            )
        }
    }

    @Test
    fun `rejects non-finite continuous coordinates`() {
        val invalidPosition =
            object : Position3Like<Position3> {
                override val x: Double = Double.POSITIVE_INFINITY
                override val y: Double = 0.0
                override val z: Double = 0.0

                override fun createPosition(
                    x: Double,
                    y: Double,
                    z: Double,
                ): Position3 = Position3(x, y, z)
            }

        assertThrows<IllegalArgumentException> {
            BlockPosition.fromPosition(invalidPosition)
        }
    }

    @Test
    fun `uses value equality`() {
        val first = BlockPosition(1, 2, 3)
        val second = BlockPosition(1, 2, 3)

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertEquals(first, first.copy())
    }

    @Test
    fun `converts to mutable position`() {
        val position = BlockPosition(1, 2, 3)

        assertEquals(position, position.toMutable().toImmutable())
    }
}
