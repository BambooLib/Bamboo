package io.github.deopping.bamboo.geometry.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private class MutableVector3Test {
    @Test
    fun `sets all components`() {
        val vector = MutableVector3(1.0, 2.0, 3.0)

        vector.set(4.0, 5.0, 6.0)

        assertVectorEquals(Vector3(4.0, 5.0, 6.0), vector)
    }

    @Test
    fun `supports in-place addition and subtraction`() {
        val vector = MutableVector3(1.0, 2.0, 3.0)

        vector.plusAssign(Vector3(4.0, 5.0, 6.0))
        assertVectorEquals(Vector3(5.0, 7.0, 9.0), vector)

        vector.minusAssign(Vector3(1.0, 2.0, 3.0))
        assertVectorEquals(Vector3(4.0, 5.0, 6.0), vector)
    }

    @Test
    fun `calculates cross product in place`() {
        val vector = MutableVector3(1.0, 2.0, 3.0)

        vector.crossAssign(Vector3(4.0, 5.0, 6.0))

        assertVectorEquals(Vector3(-3.0, 6.0, -3.0), vector)
    }

    @Test
    fun `supports self-aliasing during cross product`() {
        val vector = MutableVector3(1.0, 2.0, 3.0)

        vector.crossAssign(vector)

        assertVectorEquals(Vector3(0.0, 0.0, 0.0), vector)
    }

    @Test
    fun `negates components in place`() {
        val vector = MutableVector3(1.0, -2.0, 3.0)

        vector.negate()

        assertVectorEquals(Vector3(-1.0, 2.0, -3.0), vector)
    }

    @Test
    fun `normalizes in place`() {
        val vector = MutableVector3(1.0, 2.0, 3.0)

        vector.normalize()

        assertEquals(1.0, vector.length(), GEOMETRY_EPSILON)
    }

    @Test
    fun `sets vector length`() {
        val vector = MutableVector3(3.0, 0.0, 4.0)

        vector.setLength(10.0)

        assertVectorEquals(Vector3(6.0, 0.0, 8.0), vector)
        assertEquals(10.0, vector.length(), GEOMETRY_EPSILON)
    }

    @Test
    fun `lerps in place`() {
        val vector = MutableVector3(1.0, 2.0, 3.0)

        vector.lerpAssign(Vector3(4.0, 5.0, 6.0), 0.5)

        assertVectorEquals(Vector3(2.5, 3.5, 4.5), vector)
    }

    @Test
    fun `does not partially mutate when set receives invalid components`() {
        val vector = MutableVector3(1.0, 2.0, 3.0)

        assertThrows<IllegalArgumentException> {
            vector.set(4.0, Double.NaN, 6.0)
        }

        assertVectorEquals(Vector3(1.0, 2.0, 3.0), vector)
    }

    @Test
    fun `does not partially mutate when lerp receives invalid progress`() {
        val vector = MutableVector3(1.0, 2.0, 3.0)

        assertThrows<IllegalArgumentException> {
            vector.lerpAssign(Vector3(4.0, 5.0, 6.0), Double.NaN)
        }

        assertVectorEquals(Vector3(1.0, 2.0, 3.0), vector)
    }

    @Test
    fun `creates an independent immutable snapshot`() {
        val vector = MutableVector3(1.0, 2.0, 3.0)
        val snapshot = vector.toImmutable()

        vector.set(4.0, 5.0, 6.0)

        assertVectorEquals(Vector3(1.0, 2.0, 3.0), snapshot)
        assertVectorEquals(Vector3(4.0, 5.0, 6.0), vector)
    }
}
