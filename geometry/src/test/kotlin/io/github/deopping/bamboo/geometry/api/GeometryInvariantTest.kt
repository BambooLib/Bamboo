package io.github.deopping.bamboo.geometry.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private class GeometryInvariantTest {
    @Test
    fun `dot product is symmetric and cross product is anti-symmetric`() {
        val vectors =
            listOf(
                Vector3(1.0, 2.0, 3.0),
                Vector3(-4.0, 5.0, 6.0),
                Vector3(0.5, -2.5, 8.0),
                Vector3(0.0, 1.0, 0.0),
            )

        for (first in vectors) {
            for (second in vectors) {
                assertEquals(first.dot(second), second.dot(first), GEOMETRY_EPSILON)
                assertVectorEquals(first.cross(second), -second.cross(first))
            }
        }
    }

    @Test
    fun `position distances are symmetric and displacements are reversible`() {
        val positions =
            listOf(
                Position3(0.0, 0.0, 0.0),
                Position3(10.0, 64.0, -20.0),
                Position3(-100.0, 20.0, 50.0),
            )

        for (first in positions) {
            for (second in positions) {
                assertEquals(
                    first.distanceTo(second),
                    second.distanceTo(first),
                    GEOMETRY_EPSILON,
                )

                assertVectorEquals(first.vectorTo(second), -second.vectorTo(first))
            }
        }
    }

    @Test
    fun `rotation direction conversion round trips representation angles`() {
        val yaws = listOf(-179.0, -90.0, -45.0, 0.0, 45.0, 90.0, 179.0, 360.0)
        val pitches = listOf(-90.0, -45.0, 0.0, 45.0, 90.0)

        for (yaw in yaws) {
            for (pitch in pitches) {
                val original = Rotation(yaw, pitch)
                val reconstructed = Rotation.fromDirection(original.toDirection())

                assertEquals(
                    RotationLike.normalizeDegrees(yaw),
                    reconstructed.yaw,
                    GEOMETRY_EPSILON,
                )

                assertEquals(pitch, reconstructed.pitch, GEOMETRY_EPSILON)
            }
        }
    }
}
