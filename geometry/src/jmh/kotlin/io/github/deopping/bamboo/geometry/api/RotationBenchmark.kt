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
 * Benchmarks immutable and mutable rotation operations.
 *
 * Direction conversion benchmarks use rotations prepared before benchmarking.
 * A separate benchmark measures immutable rotation construction.
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
open class RotationBenchmark {
    /**
     * Immutable rotation used by the benchmarks.
     * @since 1.0.0
     */
    private lateinit var rotation: Rotation

    /**
     * Mutable rotation used by the benchmarks.
     * @since 1.0.0
     */
    private lateinit var mutableRotation: MutableRotation

    /**
     * Prepares the rotations before benchmarking.
     * @since 1.0.0
     */
    @Setup(Level.Trial)
    fun setup() {
        rotation = Rotation(137.5, 24.5)
        mutableRotation = MutableRotation(137.5, 24.5)
    }

    /**
     * Measures direction conversion using an immutable rotation.
     *
     * @return the direction vector
     * @since 1.0.0
     */
    @Benchmark
    fun immutableToDirection(): Vector3 = rotation.toDirection()

    /**
     * Measures direction conversion using a mutable rotation.
     *
     * @return the direction vector
     * @since 1.0.0
     */
    @Benchmark
    fun mutableToDirection(): Vector3 = mutableRotation.toDirection()

    /**
     * Measures construction of an immutable rotation.
     *
     * @return the newly created rotation
     * @since 1.0.0
     */
    @Benchmark
    fun construction(): Rotation = Rotation(137.5, 24.5)

    /**
     * Benchmarks rotation construction followed by direction conversion.
     *
     * @return the resulting direction vector
     * @since 1.0.0
     */
    @Benchmark
    fun constructionAndDirection(): Vector3 = Rotation(45.0, 30.0).toDirection()

    /**
     * Benchmarks repeated direction retrieval from a cached rotation.
     *
     * @return the cached direction vector
     * @since 1.0.0
     */
    @Benchmark
    fun repeatedDirection(): Vector3 = rotation.toDirection()

    /**
     * Benchmarks mutable rotation construction.
     *
     * @return a newly constructed mutable rotation
     * @since 1.0.0
     */
    @Benchmark
    fun mutableConstruction(): MutableRotation = MutableRotation(45.0, 30.0)

    /**
     * Benchmarks mutable rotation construction followed by direction conversion.
     *
     * @return the resulting direction vector
     * @since 1.0.0
     */
    @Benchmark
    fun mutableConstructionAndDirection(): Vector3 = MutableRotation(45.0, 30.0).toDirection()
}
