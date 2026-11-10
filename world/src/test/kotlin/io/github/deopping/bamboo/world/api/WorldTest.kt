package io.github.deopping.bamboo.world.api

import io.github.deopping.bamboo.geometry.api.Rotation
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID
import java.util.concurrent.CompletableFuture

private class WorldTest {
    private val world = TestWorld(worldId)

    @Test
    fun `containsPosition returns true for positions in the same world`() {
        val position = WorldPosition(worldId, 10.0, 64.0, 20.0)

        assertTrue(world.containsPosition(position))
    }

    @Test
    fun `containsPosition returns true for locations in the same world`() {
        val location =
            WorldLocation(
                worldId,
                10.0,
                64.0,
                20.0,
                Rotation.ZERO,
            )

        assertTrue(world.containsPosition(location))
    }

    @Test
    fun `containsPosition returns false for positions in another world`() {
        val position = WorldPosition(otherWorldId, 10.0, 64.0, 20.0)

        assertFalse(world.containsPosition(position))
    }

    private class TestWorld(
        override val uniqueId: UUID,
    ) : World {
        override val name: String = "Test World"
        override val minHeight: Int = -64
        override val maxHeight: Int = 320

        override fun isLoaded(): CompletableFuture<Boolean> = CompletableFuture.completedFuture(true)

        override fun getSpawnLocation(): CompletableFuture<WorldLocation> =
            CompletableFuture.completedFuture(
                WorldLocation(
                    uniqueId,
                    0.0,
                    64.0,
                    0.0,
                    Rotation.ZERO,
                ),
            )

        override fun setSpawnLocation(location: WorldLocation): CompletableFuture<Boolean> =
            CompletableFuture.completedFuture(true)

        override fun getTimeOfDay(): CompletableFuture<Long> = CompletableFuture.completedFuture(0L)

        override fun setTimeOfDay(timeOfDay: Long): CompletableFuture<Boolean> = CompletableFuture.completedFuture(true)

        override fun getFullTime(): CompletableFuture<Long> = CompletableFuture.completedFuture(0L)

        override fun setFullTime(fullTime: Long): CompletableFuture<Boolean> = CompletableFuture.completedFuture(true)

        override fun getWeather(): CompletableFuture<WorldWeather> =
            CompletableFuture.completedFuture(WorldWeather.CLEAR)

        override fun setWeather(weather: WorldWeather): CompletableFuture<Boolean> =
            CompletableFuture.completedFuture(true)
    }
}
