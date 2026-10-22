package io.github.deopping.bamboo.reflection

import io.github.deopping.bamboo.reflection.api.MethodAccessor
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
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

/**
 * Benchmarks Bamboo's method accessor against Java reflection.
 *
 * Invocation benchmarks exclude method lookup and accessor creation.
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
open class MethodAccessorBenchmark {

    /**
     * Target type instantiated by the benchmarks.
     *
     * @since 0.1.0
     */
    private class BenchmarkTarget {
        fun add(a: Int, b: Int): Int = a + b
    }

    private lateinit var target: BenchmarkTarget
    private lateinit var method: Method
    private lateinit var accessor: MethodAccessor
    private lateinit var reflection: ReflectionApi

    /**
     * Prepares the target, method, API, and accessor before benchmarking.
     *
     * @since 0.1.0
     */
    @Setup
    fun setup() {
        target = BenchmarkTarget()

        method = BenchmarkTarget::class.java.getDeclaredMethod(
            "add",
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
        )

        reflection = ReflectionApi.create()
        accessor = reflection.createMethodAccessor(method)
    }

    /**
     * Measures invocation using Java reflection.
     *
     * @return the result of the method invocation
     * @since 0.1.0
     */
    @Benchmark
    fun javaReflection(): Any? =
        method.invoke(target, 2, 3)

    /**
     * Measures invocation using Bamboo's prepared method accessor.
     *
     * @return the result of the method invocation
     * @since 0.1.0
     */
    @Benchmark
    fun bambooAccessor(): Any? =
        accessor.invoke(target, 2, 3)

    /**
     * Measures direct invocation as a baseline.
     *
     * @return the result of the method invocation
     * @since 0.1.0
     */
    @Benchmark
    fun directInvocation(): Int =
        target.add(2, 3)

    /**
     * Measures retrieval of an accessor already held in the cache.
     *
     * Includes the cache lookup, but not accessor creation.
     *
     * @return the cached method accessor
     * @since 0.1.0
     */
    @Benchmark
    fun cachedAccessorLookup(): MethodAccessor =
        reflection.createMethodAccessor(method)

}