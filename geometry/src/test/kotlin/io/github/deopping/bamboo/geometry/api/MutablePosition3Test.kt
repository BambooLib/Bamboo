package io.github.deopping.bamboo.geometry.api

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private class MutablePosition3Test {
    @Test
    fun `sets all components`() {
        val position = MutablePosition3(1.0, 2.0, 3.0)

        position.set(4.0, 5.0, 6.0)

        assertPositionEquals(Position3(4.0, 5.0, 6.0), position)
    }

    @Test
    fun `supports in-place vector addition and subtraction`() {
        val position = MutablePosition3(1.0, 2.0, 3.0)
        val vector = Vector3(4.0, 5.0, 6.0)

        position.plusAssign(vector)
        assertPositionEquals(Position3(5.0, 7.0, 9.0), position)

        position.minusAssign(vector)
        assertPositionEquals(Position3(1.0, 2.0, 3.0), position)
    }

    @Test
    fun `lerps in place`() {
        val position = MutablePosition3(1.0, 2.0, 3.0)

        position.lerpAssign(Position3(4.0, 5.0, 6.0), 0.5)

        assertPositionEquals(Position3(2.5, 3.5, 4.5), position)
    }

    @Test
    fun `does not partially mutate when set receives invalid components`() {
        val position = MutablePosition3(1.0, 2.0, 3.0)

        assertThrows<IllegalArgumentException> {
            position.set(4.0, Double.NaN, 6.0)
        }

        assertPositionEquals(Position3(1.0, 2.0, 3.0), position)
    }

    @Test
    fun `does not partially mutate when lerp receives invalid progress`() {
        val position = MutablePosition3(1.0, 2.0, 3.0)

        assertThrows<IllegalArgumentException> {
            position.lerpAssign(Position3(4.0, 5.0, 6.0), Double.NaN)
        }

        assertPositionEquals(Position3(1.0, 2.0, 3.0), position)
    }

    @Test
    fun `creates an independent immutable snapshot`() {
        val position = MutablePosition3(1.0, 2.0, 3.0)
        val snapshot = position.toImmutable()

        position.set(4.0, 5.0, 6.0)

        assertPositionEquals(Position3(1.0, 2.0, 3.0), snapshot)
        assertPositionEquals(Position3(4.0, 5.0, 6.0), position)
    }
}
