package io.github.deopping.bamboo.world.api

import io.github.deopping.bamboo.geometry.api.Position3
import io.github.deopping.bamboo.geometry.api.Vector3
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private class MutableWorldPositionTest {
    @Test
    fun `constructs with expected properties`() {
        val position = MutableWorldPosition(worldId, 10.0, 64.0, 20.0)

        assertEquals(worldId, position.worldId)
        assertEquals(10.0, position.x)
        assertEquals(64.0, position.y)
        assertEquals(20.0, position.z)
    }

    @Test
    fun `rejects non-finite coordinates`() {
        val invalidCoordinates =
            listOf(
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
            )

        invalidCoordinates.forEach { coordinate ->
            assertThrows<IllegalArgumentException> {
                MutableWorldPosition(worldId, coordinate, 0.0, 0.0)
            }

            assertThrows<IllegalArgumentException> {
                MutableWorldPosition(worldId, 0.0, coordinate, 0.0)
            }

            assertThrows<IllegalArgumentException> {
                MutableWorldPosition(worldId, 0.0, 0.0, coordinate)
            }
        }
    }

    @Test
    fun `coordinate setters update values`() {
        val position = MutableWorldPosition(worldId, 0.0, 0.0, 0.0)

        assertEquals(0.0, position.x)
        assertEquals(0.0, position.y)
        assertEquals(0.0, position.z)

        position.x = 10.0
        position.y = 20.0
        position.z = 30.0

        assertEquals(10.0, position.x)
        assertEquals(20.0, position.y)
        assertEquals(30.0, position.z)
    }

    @Test
    fun `coordinate setters reject non-finite values`() {
        val position = MutableWorldPosition(worldId, 10.0, 20.0, 30.0)

        assertThrows<IllegalArgumentException> {
            position.x = Double.NaN
        }

        assertThrows<IllegalArgumentException> {
            position.y = Double.NaN
        }

        assertThrows<IllegalArgumentException> {
            position.z = Double.NaN
        }

        assertEquals(10.0, position.x)
        assertEquals(20.0, position.y)
        assertEquals(30.0, position.z)
    }

    @Test
    fun `world identifier can be changed`() {
        val position = MutableWorldPosition(worldId, 10.0, 20.0, 30.0)

        position.worldId = otherWorldId

        assertEquals(otherWorldId, position.worldId)
    }

    @Test
    fun `set replaces world and coordinates`() {
        val position = MutableWorldPosition(worldId, 1.0, 2.0, 3.0)

        val result = position.set(otherWorldId, 10.0, 20.0, 30.0)

        assertSame(position, result)
        assertEquals(WorldPosition(otherWorldId, 10.0, 20.0, 30.0), position.toImmutable())
    }

    @Test
    fun `set validates all coordinates before modification`() {
        val position = MutableWorldPosition(worldId, 1.0, 2.0, 3.0)
        val original = position.toImmutable()

        assertThrows<IllegalArgumentException> {
            position.set(otherWorldId, 10.0, Double.NaN, 30.0)
        }

        assertEquals(original, position.toImmutable())
    }

    @Test
    fun `set copies another world position`() {
        val position = MutableWorldPosition(worldId, 1.0, 2.0, 3.0)
        val other = WorldPosition(otherWorldId, 10.0, 20.0, 30.0)

        assertSame(position, position.set(other))
        assertEquals(other, position.toImmutable())
    }

    @Test
    fun `set copies geometry coordinates with supplied world`() {
        val position = MutableWorldPosition(worldId, 1.0, 2.0, 3.0)
        val geometryPosition = Position3(10.0, 20.0, 30.0)

        position.set(otherWorldId, geometryPosition)

        assertEquals(WorldPosition(otherWorldId, 10.0, 20.0, 30.0), position.toImmutable())
    }

    @Test
    fun `plusAssign moves position`() {
        val position = MutableWorldPosition(worldId, 10.0, 20.0, 30.0)

        position += Vector3(5.0, 2.0, 3.0)

        assertEquals(WorldPosition(worldId, 15.0, 22.0, 33.0), position.toImmutable())
    }

    @Test
    fun `minusAssign moves position in reverse`() {
        val position = MutableWorldPosition(worldId, 10.0, 20.0, 30.0)

        position -= Vector3(5.0, 2.0, 3.0)

        assertEquals(WorldPosition(worldId, 5.0, 18.0, 27.0), position.toImmutable())
    }

    @Test
    fun `midpointAssign moves to midpoint`() {
        val position = MutableWorldPosition(worldId, 0.0, 0.0, 0.0)
        val other = WorldPosition(worldId, 10.0, 20.0, 30.0)

        assertSame(position, position.midpointAssign(other))

        assertEquals(
            WorldPosition(worldId, 5.0, 10.0, 15.0),
            position.toImmutable(),
        )
    }

    @Test
    fun `midpointAssign rejects different worlds without modification`() {
        val position = MutableWorldPosition(worldId, 0.0, 0.0, 0.0)
        val original = position.toImmutable()
        val other = WorldPosition(otherWorldId, 10.0, 20.0, 30.0)

        assertThrows<IllegalArgumentException> {
            position.midpointAssign(other)
        }

        assertEquals(original, position.toImmutable())
    }

    @Test
    fun `lerpAssign interpolates position`() {
        val position = MutableWorldPosition(worldId, 0.0, 0.0, 0.0)
        val other = WorldPosition(worldId, 10.0, 20.0, 30.0)

        assertSame(position, position.lerpAssign(other, 0.5))

        assertEquals(
            WorldPosition(worldId, 5.0, 10.0, 15.0),
            position.toImmutable(),
        )
    }

    @Test
    fun `lerpAssign supports extrapolation`() {
        val position = MutableWorldPosition(worldId, 0.0, 0.0, 0.0)
        val other = WorldPosition(worldId, 10.0, 20.0, 30.0)

        position.lerpAssign(other, 1.5)

        assertEquals(
            WorldPosition(worldId, 15.0, 30.0, 45.0),
            position.toImmutable(),
        )
    }

    @Test
    fun `lerpAssign handles zero and one progress`() {
        val position = MutableWorldPosition(worldId, 0.0, 0.0, 0.0)
        val other = WorldPosition(worldId, 10.0, 20.0, 30.0)

        assertSame(position, position.lerpAssign(other, 0.0))
        assertEquals(WorldPosition(worldId, 0.0, 0.0, 0.0), position.toImmutable())

        position.lerpAssign(other, 1.0)
        assertEquals(other, position.toImmutable())
    }

    @Test
    fun `lerpAssign rejects invalid progress without modification`() {
        val position = MutableWorldPosition(worldId, 1.0, 2.0, 3.0)
        val original = position.toImmutable()
        val other = WorldPosition(worldId, 10.0, 20.0, 30.0)

        assertThrows<IllegalArgumentException> {
            position.lerpAssign(other, Double.NaN)
        }

        assertEquals(original, position.toImmutable())
    }

    @Test
    fun `lerpAssign rejects different worlds without modification`() {
        val position = MutableWorldPosition(worldId, 1.0, 2.0, 3.0)
        val original = position.toImmutable()
        val other = WorldPosition(otherWorldId, 10.0, 20.0, 30.0)

        assertThrows<IllegalArgumentException> {
            position.lerpAssign(other, 0.5)
        }

        assertEquals(original, position.toImmutable())
    }

    @Test
    fun `inherited arithmetic produces immutable world positions`() {
        val position = MutableWorldPosition(worldId, 10.0, 20.0, 30.0)

        val result: WorldPosition = position + Vector3(1.0, 2.0, 3.0)

        assertEquals(WorldPosition(worldId, 11.0, 22.0, 33.0), result)
        assertEquals(WorldPosition(worldId, 10.0, 20.0, 30.0), position.toImmutable())
    }

    @Test
    fun `toImmutable creates independent snapshot`() {
        val position = MutableWorldPosition(worldId, 10.0, 20.0, 30.0)

        val snapshot = position.toImmutable()

        position.x = 100.0
        position.worldId = otherWorldId

        assertEquals(WorldPosition(worldId, 10.0, 20.0, 30.0), snapshot)
    }

    @Test
    fun `mutable positions use reference equality`() {
        val first = MutableWorldPosition(worldId, 10.0, 20.0, 30.0)
        val second = MutableWorldPosition(worldId, 10.0, 20.0, 30.0)

        assertNotEquals(first, second)
    }
}
