package io.github.deopping.bamboo.world.api

import io.github.deopping.bamboo.geometry.api.MutableRotation
import io.github.deopping.bamboo.geometry.api.Rotation
import io.github.deopping.bamboo.geometry.api.RotationLike
import io.github.deopping.bamboo.geometry.api.Vector3
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private class MutableWorldLocationTest {
    @Test
    fun `constructs with expected properties`() {
        val location =
            MutableWorldLocation(
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
        assertEquals(90.0, location.rotation.yaw)
        assertEquals(30.0, location.rotation.pitch)
    }

    @Test
    fun `constructor copies supplied mutable rotation`() {
        val rotation = MutableRotation(90.0, 30.0)

        val location =
            MutableWorldLocation(
                worldId,
                10.0,
                64.0,
                20.0,
                rotation,
            )

        rotation.yaw = 180.0
        rotation.pitch = 60.0

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
                MutableWorldLocation(worldId, coordinate, 0.0, 0.0, Rotation.ZERO)
            }

            assertThrows<IllegalArgumentException> {
                MutableWorldLocation(worldId, 0.0, coordinate, 0.0, Rotation.ZERO)
            }

            assertThrows<IllegalArgumentException> {
                MutableWorldLocation(worldId, 0.0, 0.0, coordinate, Rotation.ZERO)
            }
        }
    }

    @Test
    fun `coordinate setters update values`() {
        val location =
            MutableWorldLocation(
                worldId,
                0.0,
                0.0,
                0.0,
                Rotation.ZERO,
            )

        location.x = 10.0
        location.y = 20.0
        location.z = 30.0

        assertEquals(10.0, location.x)
        assertEquals(20.0, location.y)
        assertEquals(30.0, location.z)
    }

    @Test
    fun `coordinate setters reject non-finite values`() {
        val location =
            MutableWorldLocation(
                worldId,
                10.0,
                20.0,
                30.0,
                Rotation.ZERO,
            )

        assertThrows<IllegalArgumentException> {
            location.x = Double.NaN
        }

        assertThrows<IllegalArgumentException> {
            location.y = Double.POSITIVE_INFINITY
        }

        assertThrows<IllegalArgumentException> {
            location.z = Double.NEGATIVE_INFINITY
        }

        assertEquals(10.0, location.x)
        assertEquals(20.0, location.y)
        assertEquals(30.0, location.z)
    }

    @Test
    fun `rotation reference remains stable`() {
        val location =
            MutableWorldLocation(
                worldId,
                10.0,
                64.0,
                20.0,
                Rotation(90.0, 30.0),
            )

        val originalRotation = location.rotation

        location.setRotation(Rotation(180.0, 45.0))

        assertSame(originalRotation, location.rotation)
        assertEquals(180.0, location.rotation.yaw)
        assertEquals(45.0, location.rotation.pitch)
    }

    @Test
    fun `rotation can be modified directly`() {
        val location =
            MutableWorldLocation(
                worldId,
                10.0,
                64.0,
                20.0,
                Rotation.ZERO,
            )

        location.rotation.yaw = 90.0
        location.rotation.pitch = 30.0

        assertEquals(90.0, location.rotation.yaw)
        assertEquals(30.0, location.rotation.pitch)
    }

    @Test
    fun `set replaces all properties`() {
        val location =
            MutableWorldLocation(
                worldId,
                1.0,
                2.0,
                3.0,
                Rotation.ZERO,
            )

        val result =
            location.set(
                otherWorldId,
                10.0,
                20.0,
                30.0,
                Rotation(90.0, 45.0),
            )

        assertSame(location, result)

        assertEquals(
            WorldLocation(otherWorldId, 10.0, 20.0, 30.0, Rotation(90.0, 45.0)),
            location.toImmutable(),
        )
    }

    @Test
    fun `set validates coordinates before modification`() {
        val location =
            MutableWorldLocation(
                worldId,
                1.0,
                2.0,
                3.0,
                Rotation(90.0, 30.0),
            )

        val original = location.toImmutable()

        assertThrows<IllegalArgumentException> {
            location.set(
                otherWorldId,
                10.0,
                Double.NaN,
                30.0,
                Rotation.ZERO,
            )
        }

        assertEquals(original, location.toImmutable())
    }

    @Test
    fun `set validates rotation before modification`() {
        val location =
            MutableWorldLocation(
                worldId,
                1.0,
                2.0,
                3.0,
                Rotation(90.0, 30.0),
            )

        val original = location.toImmutable()

        val invalidRotation =
            object : RotationLike {
                override val yaw: Double = 90.0
                override val pitch: Double = Double.NaN
            }

        assertThrows<IllegalArgumentException> {
            location.set(
                otherWorldId,
                10.0,
                20.0,
                30.0,
                invalidRotation,
            )
        }

        assertEquals(original, location.toImmutable())
    }

    @Test
    fun `set copies another world location`() {
        val location =
            MutableWorldLocation(
                worldId,
                1.0,
                2.0,
                3.0,
                Rotation.ZERO,
            )

        val other =
            WorldLocation(
                otherWorldId,
                10.0,
                20.0,
                30.0,
                Rotation(180.0, 45.0),
            )

        assertSame(location, location.set(other))
        assertEquals(other, location.toImmutable())
    }

    @Test
    fun `setPosition copies position and preserves rotation`() {
        val location =
            MutableWorldLocation(
                worldId,
                1.0,
                2.0,
                3.0,
                Rotation(90.0, 30.0),
            )

        val position = WorldPosition(otherWorldId, 10.0, 20.0, 30.0)

        assertSame(location, location.setPosition(position))

        assertEquals(otherWorldId, location.worldId)
        assertEquals(10.0, location.x)
        assertEquals(20.0, location.y)
        assertEquals(30.0, location.z)
        assertEquals(Rotation(90.0, 30.0), location.rotation.toImmutable())
    }

    @Test
    fun `setRotation copies supplied rotation`() {
        val location =
            MutableWorldLocation(
                worldId,
                10.0,
                64.0,
                20.0,
                Rotation.ZERO,
            )

        val suppliedRotation = MutableRotation(90.0, 30.0)

        location.setRotation(suppliedRotation)

        suppliedRotation.yaw = 180.0

        assertEquals(90.0, location.rotation.yaw)
    }

    @Test
    fun `moveBy changes coordinates and preserves rotation`() {
        val location =
            MutableWorldLocation(
                worldId,
                10.0,
                20.0,
                30.0,
                Rotation(90.0, 30.0),
            )

        assertSame(location, location.moveBy(Vector3(5.0, 2.0, 3.0)))

        assertEquals(
            WorldLocation(worldId, 15.0, 22.0, 33.0, Rotation(90.0, 30.0)),
            location.toImmutable(),
        )
    }

    @Test
    fun `plusAssign moves location`() {
        val location =
            MutableWorldLocation(
                worldId,
                10.0,
                20.0,
                30.0,
                Rotation.ZERO,
            )

        location += Vector3(5.0, 2.0, 3.0)

        assertEquals(
            WorldLocation(worldId, 15.0, 22.0, 33.0, Rotation.ZERO),
            location.toImmutable(),
        )
    }

    @Test
    fun `minusAssign moves location in reverse`() {
        val location =
            MutableWorldLocation(
                worldId,
                10.0,
                20.0,
                30.0,
                Rotation.ZERO,
            )

        location -= Vector3(5.0, 2.0, 3.0)

        assertEquals(
            WorldLocation(worldId, 5.0, 18.0, 27.0, Rotation.ZERO),
            location.toImmutable(),
        )
    }

    @Test
    fun `midpointAssign interpolates position and preserves rotation`() {
        val location =
            MutableWorldLocation(
                worldId,
                0.0,
                0.0,
                0.0,
                Rotation(90.0, 30.0),
            )

        val other =
            WorldLocation(
                worldId,
                10.0,
                20.0,
                30.0,
                Rotation(180.0, 45.0),
            )

        assertSame(location, location.midpointAssign(other))

        assertEquals(
            WorldLocation(worldId, 5.0, 10.0, 15.0, Rotation(90.0, 30.0)),
            location.toImmutable(),
        )
    }

    @Test
    fun `midpointAssign rejects different worlds without modification`() {
        val location =
            MutableWorldLocation(
                worldId,
                0.0,
                0.0,
                0.0,
                Rotation.ZERO,
            )

        val original = location.toImmutable()
        val other = WorldLocation(otherWorldId, 10.0, 20.0, 30.0, Rotation.ZERO)

        assertThrows<IllegalArgumentException> {
            location.midpointAssign(other)
        }

        assertEquals(original, location.toImmutable())
    }

    @Test
    fun `lerpAssign interpolates position and preserves rotation`() {
        val location =
            MutableWorldLocation(
                worldId,
                0.0,
                0.0,
                0.0,
                Rotation(90.0, 30.0),
            )

        val other =
            WorldLocation(
                worldId,
                10.0,
                20.0,
                30.0,
                Rotation(180.0, 45.0),
            )

        assertSame(location, location.lerpAssign(other, 0.5))

        assertEquals(
            WorldLocation(worldId, 5.0, 10.0, 15.0, Rotation(90.0, 30.0)),
            location.toImmutable(),
        )
    }

    @Test
    fun `lerpAssign with one copies position and preserves rotation`() {
        val location =
            MutableWorldLocation(
                worldId,
                0.0,
                0.0,
                0.0,
                Rotation(90.0, 30.0),
            )

        val other =
            WorldLocation(
                worldId,
                10.0,
                20.0,
                30.0,
                Rotation(180.0, 45.0),
            )

        location.lerpAssign(other, 1.0)

        assertEquals(
            WorldLocation(worldId, 10.0, 20.0, 30.0, Rotation(90.0, 30.0)),
            location.toImmutable(),
        )
    }

    @Test
    fun `inherited arithmetic produces immutable world locations`() {
        val location =
            MutableWorldLocation(
                worldId,
                10.0,
                20.0,
                30.0,
                Rotation(90.0, 30.0),
            )

        val result: WorldLocation = location + Vector3(1.0, 2.0, 3.0)

        assertEquals(
            WorldLocation(worldId, 11.0, 22.0, 33.0, Rotation(90.0, 30.0)),
            result,
        )
    }

    @Test
    fun `toImmutable creates independent location and rotation snapshots`() {
        val location =
            MutableWorldLocation(
                worldId,
                10.0,
                20.0,
                30.0,
                Rotation(90.0, 30.0),
            )

        val snapshot = location.toImmutable()

        location.x = 100.0
        location.rotation.yaw = 180.0

        assertEquals(
            WorldLocation(worldId, 10.0, 20.0, 30.0, Rotation(90.0, 30.0)),
            snapshot,
        )
    }

    @Test
    fun `mutable locations use reference equality`() {
        val first =
            MutableWorldLocation(
                worldId,
                10.0,
                20.0,
                30.0,
                Rotation.ZERO,
            )

        val second =
            MutableWorldLocation(
                worldId,
                10.0,
                20.0,
                30.0,
                Rotation.ZERO,
            )

        assertNotEquals(first, second)
    }
}
