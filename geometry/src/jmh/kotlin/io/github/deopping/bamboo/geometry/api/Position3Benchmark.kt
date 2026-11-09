package io.github.deopping.bamboo.geometry.api

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

/**
 * Measures three-dimensional position operations.
 *
 * Operations use positions prepared before benchmarking. The benchmarks
 * cover distance calculations, displacement creation, and interpolation.
 *
 * @author DeOpping
 * @since 1.0.0
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
open class Position3Benchmark {
    /**
     * Starting position used by the benchmarks.
     * @since 1.0.0
     */
    private lateinit var first: Position3

    /**
     * Target position used by the benchmarks.
     * @since 1.0.0
     */
    private lateinit var second: Position3

    /**
     * Prepares the positions before benchmarking.
     *
     * @since 1.0.0
     */
    @Setup(Level.Trial)
    fun setup() {
        first = Position3(10.0, 64.0, 10.0)
        second = Position3(110.0, 80.0, -40.0)
    }

    /**
     * Measures the distance between two positions.
     *
     * @return the distance between the positions
     * @since 1.0.0
     */
    @Benchmark
    fun distance(): Double = first.distanceTo(second)

    /**
     * Measures the squared distance between two positions.
     *
     * @return the squared distance between the positions
     * @since 1.0.0
     */
    @Benchmark
    fun distanceSquared(): Double = first.distanceSquaredTo(second)

    /**
     * Measures displacement calculation between two positions.
     *
     * @return the displacement vector
     * @since 1.0.0
     */
    @Benchmark
    fun displacement(): Vector3 = first.vectorTo(second)

    /**
     * Measures interpolation between two positions.
     *
     * @return the interpolated position
     * @since 1.0.0
     */
    @Benchmark
    fun interpolation(): Position3 = first.lerp(second, 0.5)
}
