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
 * Benchmarks immutable and mutable three-dimensional vector operations.
 *
 * Operations use vectors prepared before benchmarking. Results are returned
 * to prevent the benchmark from being optimized away.
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
open class Vector3Benchmark {
    /**
     * Vector used by the benchmarks.
     * @since 1.0.0
     */
    private lateinit var vector: Vector3

    /**
     * Second vector used by binary operations.
     * @since 1.0.0
     */
    private lateinit var other: Vector3

    /**
     * Mutable vector used by mutable-operation benchmarks.
     * @since 1.0.0
     */
    private lateinit var mutableVector: MutableVector3

    /**
     * Prepares the vectors before benchmarking.
     *
     * @since 1.0.0
     */
    @Setup(Level.Trial)
    fun setup() {
        vector = Vector3(3.0, 4.0, 12.0)
        other = Vector3(5.0, -2.0, 1.0)
        mutableVector = MutableVector3(3.0, 4.0, 12.0)
    }

    /**
     * Measures the length of an immutable vector.
     *
     * @return the vector length
     * @since 1.0.0
     */
    @Benchmark
    fun length(): Double = vector.length()

    /**
     * Measures the squared length of an immutable vector.
     *
     * @return the squared vector length
     * @since 1.0.0
     */
    @Benchmark
    fun lengthSquared(): Double = vector.lengthSquared()

    /**
     * Measures the dot product of two immutable vectors.
     *
     * @return the dot product
     * @since 1.0.0
     */
    @Benchmark
    fun dot(): Double = vector.dot(other)

    /**
     * Measures the cross product of two immutable vectors.
     *
     * @return the resulting immutable vector
     * @since 1.0.0
     */
    @Benchmark
    fun cross(): Vector3 = vector.cross(other)

    /**
     * Measures addition of two immutable vectors.
     *
     * @return the resulting immutable vector
     * @since 1.0.0
     */
    @Benchmark
    fun addition(): Vector3 = vector.plus(other)

    /**
     * Measures normalization of an immutable vector.
     *
     * @return the resulting normalized vector
     * @since 1.0.0
     */
    @Benchmark
    fun normalization(): Vector3 = vector.normalized()

    /**
     * Measures negation of an immutable vector.
     *
     * @return the resulting immutable vector
     * @since 1.0.0
     */
    @Benchmark
    fun immutableNegation(): Vector3 = vector.unaryMinus()

    /**
     * Measures negation of a mutable vector.
     *
     * @return the resulting mutable vector
     * @since 1.0.0
     */
    @Benchmark
    fun mutableNegation(): MutableVector3 = mutableVector.negate()
}
