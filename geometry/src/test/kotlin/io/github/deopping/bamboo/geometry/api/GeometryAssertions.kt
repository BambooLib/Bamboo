package io.github.deopping.bamboo.geometry.api

import org.junit.jupiter.api.Assertions.assertEquals

internal const val GEOMETRY_EPSILON: Double = 1.0e-10

internal fun assertVectorEquals(
    expected: Vector3Like,
    actual: Vector3Like,
    delta: Double = GEOMETRY_EPSILON,
) {
    assertEquals(expected.x, actual.x, delta, "X component")
    assertEquals(expected.y, actual.y, delta, "Y component")
    assertEquals(expected.z, actual.z, delta, "Z component")
}

internal fun assertPositionEquals(
    expected: Position3Like<*>,
    actual: Position3Like<*>,
    delta: Double = GEOMETRY_EPSILON,
) {
    assertEquals(expected.x, actual.x, delta, "X component")
    assertEquals(expected.y, actual.y, delta, "Y component")
    assertEquals(expected.z, actual.z, delta, "Z component")
}

internal fun assertRotationEquals(
    expected: RotationLike,
    actual: RotationLike,
    delta: Double = GEOMETRY_EPSILON,
) {
    assertEquals(expected.yaw, actual.yaw, delta, "Yaw")
    assertEquals(expected.pitch, actual.pitch, delta, "Pitch")
}
