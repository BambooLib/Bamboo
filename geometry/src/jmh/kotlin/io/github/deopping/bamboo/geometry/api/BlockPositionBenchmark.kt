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
 * Benchmarks immutable and mutable block position operations.
 *
 * Measures coordinate arithmetic, distance calculations, conversions,
 * and allocation-producing operations.
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
open class BlockPositionBenchmark {
    /**
     * Starting immutable block position.
     * @since 1.0.0
     */
    private lateinit var first: BlockPosition

    /**
     * Target immutable block position.
     * @since 1.0.0
     */
    private lateinit var second: BlockPosition

    /**
     * Mutable position used by mutation benchmarks
     * @since 1.0.0
     */
    private lateinit var mutable: MutableBlockPosition

    /**
     * Continuous position used for block-coordinate conversion.
     * @since 1.0.0
     */
    private lateinit var continuous: Position3

    /**
     * Alternates the direction of mutable offset operations.
     * @since 1.0.0
     */
    private var offsetDirection: Int = 1

    /**
     * Initializes benchmark inputs.
     * @since 1.0.0
     */
    @Setup(Level.Trial)
    fun setup() {
        first = BlockPosition(10, 20, 30)
        second = BlockPosition(100, 200, 300)
        mutable = MutableBlockPosition(10, 20, 30)
        continuous = Position3(10.75, 20.25, -30.5)
    }

    /**
     * Measures immutable block position construction.
     *
     * @return a newly constructed block position
     * @since 1.0.0
     */
    @Benchmark
    fun construction(): BlockPosition = BlockPosition(10, 20, 30)

    /**
     * Measures mutable block position construction.
     *
     * @return a newly constructed mutable block position
     * @since 1.0.0
     */
    @Benchmark
    fun mutableConstruction(): MutableBlockPosition = MutableBlockPosition(10, 20, 30)

    /**
     * Measures squared distance calculation.
     *
     * @return the squared distance
     * @since 1.0.0
     */
    @Benchmark
    fun distanceSquared(): Double = first.distanceSquaredTo(second)

    /**
     * Measures distance calculation.
     *
     * @return the distance
     * @since 1.0.0
     */
    @Benchmark
    fun distance(): Double = first.distanceTo(second)

    /**
     * Measures displacement calculation.
     *
     * @return the displacement vector
     * @since 1.0.0
     */
    @Benchmark
    fun displacement(): Vector3 = first.vectorTo(second)

    /**
     * Measures midpoint calculation.
     *
     * @return the midpoint
     * @since 1.0.0
     */
    @Benchmark
    fun midpoint(): Position3 = first.midpoint(second)

    /**
     * Measures immutable block position offset.
     *
     * @return the resulting block position
     * @since 1.0.0
     */
    @Benchmark
    fun immutableOffset(): BlockPosition = first.offset(1, -2, 3)

    /**
     * Measures mutable block position offset.
     *
     * Alternates the offset direction to keep the benchmark state bounded.
     *
     * @return the mutated block position
     * @since 1.0.0
     */
    @Benchmark
    fun mutableOffset(): MutableBlockPosition {
        offsetDirection *= -1
        return mutable.offsetAssign(offsetDirection, 0, 0)
    }

    /**
     * Measures conversion from a continuous position to block coordinates.
     *
     * @return the resulting block position
     * @since 1.0.0
     */
    @Benchmark
    fun conversion(): BlockPosition = BlockPosition.fromPosition(continuous)

    /**
     * Measures conversion to a block center.
     *
     * @return the block center
     * @since 1.0.0
     */
    @Benchmark
    fun center(): Position3 = first.center()
}
