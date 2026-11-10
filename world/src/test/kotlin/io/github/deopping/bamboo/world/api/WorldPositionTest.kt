package io.github.deopping.bamboo.world.api

import io.github.deopping.bamboo.geometry.api.Position3
import io.github.deopping.bamboo.geometry.api.Vector3
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

private class WorldPositionTest {
    private val worldId = UUID.fromString("00000000-0000-0000-0000-000000000001")
    private val otherWorldId = UUID.fromString("00000000-0000-0000-0000-000000000002")

    @Test
    fun `constructs with expected properties`() {
        val position = WorldPosition(worldId, 10.0, 64.0, 20.0)

        assertEquals(worldId, position.worldId)
        assertEquals(10.0, position.x)
        assertEquals(64.0, position.y)
        assertEquals(20.0, position.z)
    }

    @Test
    fun `constructs from geometry position`() {
        val position = Position3(10.0, 64.0, 20.0)
        val worldPosition = WorldPosition(worldId, position)

        assertEquals(worldId, worldPosition.worldId)
        assertEquals(position.x, worldPosition.x)
        assertEquals(position.y, worldPosition.y)
        assertEquals(position.z, worldPosition.z)
    }

    @Test
    fun `rejects non-finite coordinates`() {
        val invalidCoordaintes =
            listOf(
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
            )

        invalidCoordaintes.forEach { coordinate ->
            assertThrows<IllegalArgumentException> {
                WorldPosition(worldId, coordinate, 0.0, 0.0)
            }

            assertThrows<IllegalArgumentException> {
                WorldPosition(worldId, 0.0, coordinate, 0.0)
            }

            assertThrows<IllegalArgumentException> {
                WorldPosition(worldId, 0.0, 0.0, coordinate)
            }
        }
    }

    @Test
    fun `equal positions have equal hash codes`() {
        val first = WorldPosition(worldId, 10.0, 64.0, 20.0)
        val second = WorldPosition(worldId, 10.0, 64.0, 20.0)

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertEquals(first, first.copy())
    }

    @Test
    fun `different worlds produce different positions`() {
        val first = WorldPosition(worldId, 10.0, 64.0, 20.0)
        val second = WorldPosition(otherWorldId, 10.0, 64.0, 20.0)

        assertNotEquals(first, second)
    }

    @Test
    fun `different coordinates produce different positions`() {
        val first = WorldPosition(worldId, 10.0, 64.0, 20.0)
        val second = WorldPosition(worldId, 11.0, 64.0, 20.0)

        assertNotEquals(first, second)
    }

    @Test
    fun `copy preserves unchanged properties`() {
        val original = WorldPosition(worldId, 10.0, 64.0, 20.0)
        val copy = original.copy()

        assertEquals(original, copy)
        assertEquals(original.hashCode(), copy.hashCode())
    }

    @Test
    fun `copy validates changed coordinates`() {
        val original = WorldPosition(worldId, 10.0, 64.0, 20.0)

        assertThrows<IllegalArgumentException> {
            original.copy(x = Double.NaN)
        }
    }

    @Test
    fun `addition preserves world identity`() {
        val position = WorldPosition(worldId, 10.0, 64.0, 20.0)

        val result: WorldPosition = position + Vector3(5.0, 2.0, 3.0)

        assertEquals(WorldPosition(worldId, 15.0, 66.0, 23.0), result)
    }

    @Test
    fun `subtraction of vector preserves world identity`() {
        val position = WorldPosition(worldId, 10.0, 64.0, 20.0)

        val result: WorldPosition = position - Vector3(5.0, 2.0, 3.0)

        assertEquals(WorldPosition(worldId, 5.0, 62.0, 17.0), result)
    }

    @Test
    fun `subtracting another position produces displacement vector`() {
        val first = WorldPosition(worldId, 10.0, 64.0, 20.0)
        val second = WorldPosition(worldId, 15.0, 66.0, 23.0)

        assertEquals(Vector3(-5.0, -2.0, -3.0), first - second)
    }

    @Test
    fun `calculates squared distance`() {
        val first = WorldPosition(worldId, 0.0, 0.0, 0.0)
        val second = WorldPosition(worldId, 3.0, 4.0, 0.0)

        assertEquals(25.0, first.distanceSquaredTo(second))
    }

    @Test
    fun `calculates difference`() {
        val first = WorldPosition(worldId, 0.0, 0.0, 0.0)
        val second = WorldPosition(worldId, 3.0, 4.0, 0.0)

        assertEquals(5.0, first.distanceTo(second))
    }

    @Test
    fun `calculates midpoint`() {
        val first = WorldPosition(worldId, 0.0, 0.0, 0.0)
        val second = WorldPosition(worldId, 10.0, 20.0, 30.0)

        assertEquals(
            WorldPosition(worldId, 5.0, 10.0, 15.0),
            first.midpoint(second),
        )
    }

    @Test
    fun `calculates displacement vector`() {
        val first = WorldPosition(worldId, 10.0, 20.0, 30.0)
        val second = WorldPosition(worldId, 15.0, 25.0, 35.0)

        assertEquals(Vector3(5.0, 5.0, 5.0), first.vectorTo(second))
    }

    @Test
    fun `lerp interpolates between positions`() {
        val first = WorldPosition(worldId, 0.0, 0.0, 0.0)
        val second = WorldPosition(worldId, 10.0, 20.0, 30.0)

        assertEquals(
            WorldPosition(worldId, 5.0, 10.0, 15.0),
            first.lerp(second, 0.5),
        )
    }

    @Test
    fun `lerp supports extrapolation`() {
        val first = WorldPosition(worldId, 0.0, 0.0, 0.0)
        val second = WorldPosition(worldId, 10.0, 20.0, 30.0)

        assertEquals(
            WorldPosition(worldId, 15.0, 30.0, 45.0),
            first.lerp(second, 1.5),
        )
    }

    @Test
    fun `lerp handles zero and one progress`() {
        val first = WorldPosition(worldId, 0.0, 0.0, 0.0)
        val second = WorldPosition(worldId, 10.0, 20.0, 30.0)

        assertEquals(first, first.lerp(second, 0.0))
        assertEquals(second, first.lerp(second, 1.0))
    }

    @Test
    fun `lerp rejects non-finite progress`() {
        val first = WorldPosition(worldId, 0.0, 0.0, 0.0)
        val second = WorldPosition(worldId, 10.0, 20.0, 30.0)

        assertThrows<IllegalArgumentException> {
            first.lerp(second, Double.NaN)
        }

        assertThrows<IllegalArgumentException> {
            first.lerp(second, Double.POSITIVE_INFINITY)
        }

        assertThrows<IllegalArgumentException> {
            first.lerp(second, Double.NEGATIVE_INFINITY)
        }
    }

    @Test
    fun `rejects operations between different worlds`() {
        val first = WorldPosition(worldId, 0.0, 0.0, 0.0)
        val second = WorldPosition(otherWorldId, 10.0, 20.0, 30.0)

        assertThrows<IllegalArgumentException> {
            first.distanceSquaredTo(second)
        }

        assertThrows<IllegalArgumentException> {
            first.distanceTo(second)
        }

        assertThrows<IllegalArgumentException> {
            first.midpoint(second)
        }

        assertThrows<IllegalArgumentException> {
            first.vectorTo(second)
        }

        assertThrows<IllegalArgumentException> {
            first.lerp(second, 0.5)
        }

        assertThrows<IllegalArgumentException> {
            first - second
        }
    }

    @Test
    fun `rejects geometry positions without world identity`() {
        val position = WorldPosition(worldId, 0.0, 0.0, 0.0)
        val geometryPosition = Position3(10.0, 20.0, 30.0)

        assertThrows<IllegalArgumentException> {
            position.distanceTo(geometryPosition)
        }
    }

    @Test
    fun `identifies world origin`() {
        assertTrue(WorldPosition.origin(worldId).isOrigin)
        assertTrue(WorldPosition(worldId, 0.0, -0.0, 0.0).isOrigin)
    }

    @Test
    fun `toPosition3 removes world identity`() {
        val position = WorldPosition(worldId, 10.0, 64.0, 20.0)

        assertEquals(Position3(10.0, 64.0, 20.0), position.toPosition3())
    }

    @Test
    fun `toMutable creates an independent position`() {
        val original = WorldPosition(worldId, 10.0, 64.0, 20.0)
        val mutable = original.toMutable()

        mutable.x = 100.0

        assertEquals(10.0, original.x)
        assertEquals(100.0, mutable.x)
    }
}
