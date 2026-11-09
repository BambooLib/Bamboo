package io.github.deopping.bamboo.geometry.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs

private class MutableRotationTest {
    @Test
    fun `updates direction cache when yaw changes`() {
        val rotation = MutableRotation(0.0, 0.0)

        rotation.yaw = 90.0

        assertVectorEquals(Vector3(-1.0, 0.0, 0.0), rotation.toDirection())
    }

    @Test
    fun `updates direction cache when pitch changes`() {
        val rotation = MutableRotation(0.0, 0.0)

        rotation.pitch = 45.0

        assertVectorEquals(Rotation(0.0, 45.0).toDirection(), rotation.toDirection())
    }

    @Test
    fun `updates both caches when set is called`() {
        val rotation = MutableRotation(0.0, 0.0)

        rotation.set(90.0, 30.0)

        assertVectorEquals(Rotation(90.0, 30.0).toDirection(), rotation.toDirection())
    }

    @Test
    fun `rotates by yaw and pitch offsets`() {
        val rotation = MutableRotation(10.0, 20.0)

        rotation.rotate(30.0, 10.0)

        assertRotationEquals(Rotation(40.0, 30.0), rotation)
    }

    @Test
    fun `normalizes yaw without changing pitch`() {
        val rotation = MutableRotation(450.0, 30.0)

        rotation.normalize()

        assertEquals(90.0, rotation.yaw, GEOMETRY_EPSILON)
        assertEquals(30.0, rotation.pitch, GEOMETRY_EPSILON)
        assertVectorEquals(Rotation(90.0, 30.0).toDirection(), rotation.toDirection())
    }

    @Test
    fun `lerps in place using the shortest yaw path`() {
        val rotation = MutableRotation(170.0, 0.0)

        rotation.lerpAssign(Rotation(-170.0, 20.0), 0.5)

        assertEquals(180.0, abs(rotation.yaw), GEOMETRY_EPSILON)
        assertEquals(10.0, rotation.pitch, GEOMETRY_EPSILON)
        assertVectorEquals(Rotation(rotation.yaw, rotation.pitch).toDirection(), rotation.toDirection())
    }

    @Test
    fun `does not partially mutate when set receives invalid values`() {
        val rotation = MutableRotation(10.0, 20.0)

        assertThrows<IllegalArgumentException> {
            rotation.set(90.0, 91.0)
        }

        assertRotationEquals(Rotation(10.0, 20.0), rotation)
        assertVectorEquals(Rotation(10.0, 20.0).toDirection(), rotation.toDirection())
    }

    @Test
    fun `does not partially mutate when rotate exceeds pitch bounds`() {
        val rotation = MutableRotation(10.0, 80.0)

        assertThrows<IllegalArgumentException> {
            rotation.rotate(45.0, 20.0)
        }

        assertRotationEquals(Rotation(10.0, 80.0), rotation)
        assertVectorEquals(Rotation(10.0, 80.0).toDirection(), rotation.toDirection())
    }

    @Test
    fun `creates an independent immutable snapshot`() {
        val rotation = MutableRotation(90.0, 30.0)
        val snapshot = rotation.toImmutable()

        rotation.set(180.0, 0.0)

        assertRotationEquals(Rotation(90.0, 30.0), snapshot)
        assertRotationEquals(Rotation(180.0, 0.0), rotation)
    }

    @Test
    fun `toDirection reflects updated yaw`() {
        val rotation = MutableRotation(0.0, 0.0)

        assertVectorEquals(Vector3(0.0, 0.0, 1.0), rotation.toDirection())

        rotation.yaw = 90.0

        assertVectorEquals(Vector3(-1.0, 0.0, 0.0), rotation.toDirection())
    }

    @Test
    fun `toDirection reflects updated pitch`() {
        val rotation = MutableRotation(0.0, 0.0)

        assertVectorEquals(Vector3(0.0, 0.0, 1.0), rotation.toDirection())

        rotation.pitch = 90.0

        assertVectorEquals(Vector3(0.0, -1.0, 0.0), rotation.toDirection())
    }

    @Test
    fun `toDirection reflects updated angles through set`() {
        val rotation = MutableRotation(0.0, 0.0)

        rotation.toDirection()
        rotation.set(90.0, 45.0)

        assertVectorEquals(Rotation(90.0, 45.0).toDirection(), rotation.toDirection())
    }
}
