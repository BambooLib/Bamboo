package io.github.deopping.bamboo.reflection

import io.github.deopping.bamboo.reflection.api.ConstructorAccessor
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
import java.lang.reflect.Constructor
import java.util.concurrent.TimeUnit

/**
 * Benchmarks Bamboo's constructor accessor against Java reflection.
 *
 * Constructor invocation benchmarks exclude constructor lookup and accessor
 * creation. A separate benchmark measures retrieval of a cached accessor.
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
open class ConstructorAccessorBenchmark {

    /**
     * Target type instantiated by the benchmarks.
     *
     * @since 0.1.0
     */
    private class BenchmarkTarget(
        val value: Int,
        val name: String
    )

    private lateinit var constructor: Constructor<BenchmarkTarget>
    private lateinit var accessor: ConstructorAccessor<BenchmarkTarget>
    private lateinit var reflection: ReflectionApi

    /**
     * Prepares the constructor, API, and accessor before benchmarking.
     *
     * @since 0.1.0
     */
    @Setup
    fun setup() {
        constructor = BenchmarkTarget::class.java.getDeclaredConstructor(
            Int::class.javaPrimitiveType,
            String::class.java
        )

        constructor.isAccessible = true

        reflection = ReflectionApi.create()
        accessor = reflection.createConstructorAccessor(constructor)
    }

    /**
     * Measures instance creation using Java reflection.
     *
     * @return the newly created instance
     * @since 0.1.0
     */
    @Benchmark
    fun javaReflection(): Any? =
        constructor.newInstance(42, "Bamboo")

    /**
     * Measures instance creation using Bamboo's prepared constructor accessor.
     *
     * @return the newly created instance
     * @since 0.1.0
     */
    @Benchmark
    fun bambooAccessor(): Any? =
        accessor.newInstance(42, "Bamboo")

    /**
     * Measures direct constructor invocation as a baseline.
     *
     * @return the newly created instance
     * @since 0.1.0
     */
    @Benchmark
    fun directInvocation(): Any? =
        BenchmarkTarget(42, "Bamboo")

    /**
     * Measures retrieval of an accessor already held in the cache.
     *
     * Includes the cache lookup, but not accessor creation or invocation.
     *
     * @return the cached constructor accessor
     * @since 0.1.0
     */
    @Benchmark
    fun cachedAccessorLookup(): ConstructorAccessor<*> =
        reflection.createConstructorAccessor(constructor)

}