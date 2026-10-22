package io.github.deopping.bamboo.reflection

import io.github.deopping.bamboo.reflection.api.FieldAccessor
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.lang.reflect.Field
import java.util.concurrent.TimeUnit

/**
 * Benchmarks Bamboo's field accessor against Java reflection.
 *
 * Field access benchmarks exclude field lookup and accessor creation.
 * Separate benchmarks measure cached accessor retrieval.
 *
 * @author DeOpping
 * @since 0.1.0
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(2)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
open class FieldAccessorBenchmark {

    /**
     * Target type whose field is accessed by the benchmarks.
     *
     * @since 0.1.0
     */
    private class BenchmarkTarget {
        @JvmField
        var value: Int = 42
    }

    private lateinit var target: BenchmarkTarget
    private lateinit var field: Field
    private lateinit var accessor: FieldAccessor
    private lateinit var reflection: ReflectionApi

    private var nextValue = 0

    /**
     * Prepares the target, field, API, and accessor before benchmarking.
     *
     * @since 0.1.0
     */
    @Setup
    fun setup() {
        target = BenchmarkTarget()
        field = BenchmarkTarget::class.java.getDeclaredField("value")
        field.isAccessible = true

        reflection = ReflectionApi.create()
        accessor = reflection.createFieldAccessor(field)
    }

    /**
     * Measures reading a field using Java reflection.
     *
     * @return the current field value
     * @since 0.1.0
     */
    @Benchmark
    fun javaReflectionGet(): Any? =
        field.get(target)

    /**
     * Measures reading a field using Bamboo's prepared accessor.
     *
     * @return the current field value
     * @since 0.1.0
     */
    @Benchmark
    fun bambooAccessorGet(): Any? =
        accessor.get(target)

    /**
     * Measures direct field access as a baseline.
     *
     * @return the current field value
     * @since 0.1.0
     */
    @Benchmark
    fun directGet(): Int =
        target.value

    /**
     * Measures writing a field using Java reflection.
     *
     * @since 0.1.0
     */
    @Benchmark
    fun javaReflectionSet() {
        field.set(target, nextValue++)
    }

    /**
     * Measures writing a field using Bamboo's prepared accessor.
     *
     * @since 0.1.0
     */
    @Benchmark
    fun bambooAccessorSet() {
        accessor.set(target, nextValue++)
    }

    /**
     * Measures direct field writes as a baseline.
     *
     * @since 0.1.0
     */
    @Benchmark
    fun directSet() {
        target.value = nextValue++
    }

    /**
     * Measures retrieval of an accessor already held in the cache.
     *
     * Includes the cache lookup, but not accessor creation or field access.
     *
     * @return the cached field accessor
     * @since 0.1.0
     */
    @Benchmark
    fun cachedAccessorLookup(): FieldAccessor =
        reflection.createFieldAccessor(field)

}