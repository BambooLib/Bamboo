package io.github.deopping.bamboo.geometry.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private class MutableBlockPositionTest {
    @Test
    fun `gets and sets individual coordinates`() {
        val position = MutableBlockPosition(1, 2, 3)

        position.x = 4
        position.y = 5
        position.z = 6

        assertEquals(4, position.x)
        assertEquals(5, position.y)
        assertEquals(6, position.z)
    }

    @Test
    fun `sets all coordinates`() {
        val position = MutableBlockPosition(1, 2, 3)

        val result = position.set(4, 5, 6)

        assertSame(position, result)
        assertEquals(BlockPosition(4, 5, 6), position.toImmutable())
    }

    @Test
    fun `copies coordinates from another position`() {
        val position = MutableBlockPosition(0, 0, 0)

        val result = position.set(BlockPosition(4, 5, 6))

        assertSame(position, result)
        assertEquals(BlockPosition(4, 5, 6), position.toImmutable())
    }

    @Test
    fun `offsets coordinates in place`() {
        val position = MutableBlockPosition(10, 20, 30)

        val result = position.offsetAssign(1, -2, 3)

        assertSame(position, result)
        assertEquals(BlockPosition(11, 18, 33), position.toImmutable())
    }

    @Test
    fun `does not partially mutate when offset overflows`() {
        val position = MutableBlockPosition(10, Int.MAX_VALUE, 30)

        assertThrows<ArithmeticException> {
            position.offsetAssign(1, 1, 1)
        }

        assertEquals(BlockPosition(10, Int.MAX_VALUE, 30), position.toImmutable())
    }

    @Test
    fun `identifies origin after mutation`() {
        val position = MutableBlockPosition(1, 2, 3)

        assertFalse(position.isOrigin)

        position.set(0, 0, 0)

        assertTrue(position.isOrigin)
    }

    @Test
    fun `creates immutable offset without modifying original`() {
        val position = MutableBlockPosition(10, 20, 30)

        val offset = position.offset(1, -2, 3)

        assertEquals(BlockPosition(10, 20, 30), position.toImmutable())
        assertEquals(BlockPosition(11, 18, 33), offset)
    }

    @Test
    fun `calculates distance using mutable coordinates`() {
        val position = MutableBlockPosition(1, 2, 3)
        val other = BlockPosition(4, 6, 15)

        assertEquals(13.0, position.distanceTo(other), GEOMETRY_EPSILON)
        assertEquals(169.0, position.distanceSquaredTo(other), GEOMETRY_EPSILON)
    }

    @Test
    fun `calculates displacement using mutable coordinates`() {
        val position = MutableBlockPosition(1, 2, 3)
        val other = BlockPosition(4, 6, 15)

        assertVectorEquals(Vector3(3.0, 4.0, 12.0), position.vectorTo(other))
    }

    @Test
    fun `creates an independent immutable snapshot`() {
        val position = MutableBlockPosition(1, 2, 3)
        val snapshot = position.toImmutable()

        position.offsetAssign(10, 20, 30)

        assertEquals(BlockPosition(1, 2, 3), snapshot)
        assertEquals(BlockPosition(11, 22, 33), position.toImmutable())
    }

    @Test
    fun `uses reference equality`() {
        val first = MutableBlockPosition(1, 2, 3)
        val second = MutableBlockPosition(1, 2, 3)

        assertEquals(false, first == second)
    }
}
