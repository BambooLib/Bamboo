package io.github.deopping.bamboo.world.api

import io.github.deopping.bamboo.geometry.api.MutableRotation
import io.github.deopping.bamboo.geometry.api.Rotation
import io.github.deopping.bamboo.geometry.api.Vector3
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private class WorldLocationTest {
    @Test
    fun `constructs with expected properties`() {
        val location =
            WorldLocation(
                worldId,
                10.0,
                64.0,
                20.0,
                rotation,
            )

        assertEquals(worldId, location.worldId)
        assertEquals(10.0, location.x)
        assertEquals(64.0, location.y)
        assertEquals(20.0, location.z)
        assertEquals(rotation, location.rotation)
    }

    @Test
    fun `constructs from world position and rotation`() {
        val position = WorldPosition(worldId, 10.0, 64.0, 20.0)

        val location = WorldLocation(position, rotation)

        assertEquals(position.worldId, location.worldId)
        assertEquals(position.x, location.x)
        assertEquals(position.y, location.y)
        assertEquals(position.z, location.z)
        assertEquals(rotation, location.rotation)
    }

    @Test
    fun `constructor copies mutable rotation`() {
        val mutableRotation = MutableRotation(90.0, 30.0)
        val position = WorldPosition(worldId, 10.0, 64.0, 20.0)

        val location = WorldLocation(position, mutableRotation)

        mutableRotation.yaw = 180.0
        mutableRotation.pitch = 45.0

        assertEquals(90.0, location.rotation.yaw)
        assertEquals(30.0, location.rotation.pitch)
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
                WorldLocation(worldId, coordinate, 0.0, 0.0, rotation)
            }

            assertThrows<IllegalArgumentException> {
                WorldLocation(worldId, 0.0, coordinate, 0.0, rotation)
            }

            assertThrows<IllegalArgumentException> {
                WorldLocation(worldId, 0.0, 0.0, coordinate, rotation)
            }
        }
    }

    @Test
    fun `equal locations have equal hash codes`() {
        val first = WorldLocation(worldId, 10.0, 64.0, 20.0, rotation)
        val second = WorldLocation(worldId, 10.0, 64.0, 20.0, rotation)

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun `different worlds produce different locations`() {
        val first = WorldLocation(worldId, 10.0, 64.0, 20.0, rotation)
        val second = WorldLocation(otherWorldId, 10.0, 64.0, 20.0, rotation)

        assertNotEquals(first, second)
    }

    @Test
    fun `different rotations produce different locations`() {
        val first = WorldLocation(worldId, 10.0, 64.0, 20.0, Rotation(90.0, 30.0))
        val second = WorldLocation(worldId, 10.0, 64.0, 20.0, Rotation(180.0, 30.0))

        assertNotEquals(first, second)
    }

    @Test
    fun `different coordinates produce different locations`() {
        val first = WorldLocation(worldId, 10.0, 64.0, 20.0, rotation)
        val second = WorldLocation(worldId, 11.0, 64.0, 20.0, rotation)

        assertNotEquals(first, second)
    }

    @Test
    fun `copy preserves unchanged properties`() {
        val original = WorldLocation(worldId, 10.0, 64.0, 20.0, rotation)

        assertEquals(original, original.copy())
    }

    @Test
    fun `copy validates changed coordinates`() {
        val original = WorldLocation(worldId, 10.0, 64.0, 20.0, rotation)

        assertThrows<IllegalArgumentException> {
            original.copy(y = Double.NaN)
        }
    }

    @Test
    fun `addition preserves world and rotation`() {
        val location = WorldLocation(worldId, 10.0, 64.0, 20.0, rotation)

        val result: WorldLocation = location + Vector3(5.0, 2.0, 3.0)

        assertEquals(
            WorldLocation(worldId, 15.0, 66.0, 23.0, rotation),
            result,
        )
    }

    @Test
    fun `subtraction preserves world and rotation`() {
        val location = WorldLocation(worldId, 10.0, 64.0, 20.0, rotation)

        val result: WorldLocation = location - Vector3(5.0, 2.0, 3.0)

        assertEquals(
            WorldLocation(worldId, 5.0, 62.0, 17.0, rotation),
            result,
        )
    }

    @Test
    fun `midpoint preserves the original rotation`() {
        val first =
            WorldLocation(
                worldId,
                0.0,
                0.0,
                0.0,
                Rotation(90.0, 30.0),
            )

        val second =
            WorldLocation(
                worldId,
                10.0,
                20.0,
                30.0,
                Rotation(180.0, 60.0),
            )

        assertEquals(
            WorldLocation(worldId, 5.0, 10.0, 15.0, first.rotation),
            first.midpoint(second),
        )
    }

    @Test
    fun `lerp preserves the original rotation`() {
        val first =
            WorldLocation(
                worldId,
                0.0,
                0.0,
                0.0,
                Rotation(90.0, 30.0),
            )

        val second =
            WorldLocation(
                worldId,
                10.0,
                20.0,
                30.0,
                Rotation(180.0, 60.0),
            )

        assertEquals(
            WorldLocation(worldId, 5.0, 10.0, 15.0, first.rotation),
            first.lerp(second, 0.5),
        )
    }

    @Test
    fun `rejects operations between different worlds`() {
        val first = WorldLocation(worldId, 0.0, 0.0, 0.0, rotation)
        val second = WorldLocation(otherWorldId, 10.0, 20.0, 30.0, rotation)

        assertThrows<IllegalArgumentException> {
            first.distanceTo(second)
        }

        assertThrows<IllegalArgumentException> {
            first.midpoint(second)
        }

        assertThrows<IllegalArgumentException> {
            first.lerp(second, 0.5)
        }
    }

    @Test
    fun `withRotation creates location with new rotation`() {
        val location = WorldLocation(worldId, 10.0, 64.0, 20.0, rotation)
        val newRotation = Rotation(180.0, 45.0)

        val result = location.withRotation(newRotation)

        assertEquals(worldId, result.worldId)
        assertEquals(location.x, result.x)
        assertEquals(location.y, result.y)
        assertEquals(location.z, result.z)
        assertEquals(newRotation, result.rotation)
    }

    @Test
    fun `withRotation copies mutable rotation`() {
        val location = WorldLocation(worldId, 10.0, 64.0, 20.0, rotation)
        val mutableRotation = MutableRotation(180.0, 45.0)

        val result = location.withRotation(mutableRotation)

        mutableRotation.yaw = 0.0

        assertEquals(180.0, result.rotation.yaw)
    }

    @Test
    fun `toWorldLocation creates equivalent immutable location`() {
        val location = WorldLocation(worldId, 10.0, 64.0, 20.0, rotation)

        assertEquals(location, location.toWorldLocation())
    }

    @Test
    fun `toMutable creates independent location`() {
        val original = WorldLocation(worldId, 10.0, 64.0, 20.0, rotation)
        val mutable = original.toMutable()

        mutable.x = 100.0
        mutable.rotation.yaw = 180.0

        assertEquals(10.0, original.x)
        assertEquals(90.0, original.rotation.yaw)
    }
}
