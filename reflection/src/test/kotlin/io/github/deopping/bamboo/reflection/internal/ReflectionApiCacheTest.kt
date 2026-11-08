package io.github.deopping.bamboo.reflection.internal

import io.github.deopping.bamboo.reflection.ReflectionApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author DeOpping
 * @since 1.0.0
 */
private class ReflectionApiCacheTest {
    private lateinit var reflection: ReflectionApi

    private class TestTarget(var value: Int) {
        fun echo(input: String): String = input

        constructor(value: String) : this(value.toInt())
    }

    @BeforeEach
    fun setup() {
        reflection = ReflectionApi.create()
    }

    @Test
    fun `unregistering constructor accessor removes cached entry`() {
        val constructor =
            TestTarget::class.java.getConstructor(Int::class.javaPrimitiveType!!)

        val first = reflection.createConstructorAccessor(constructor)

        assertSame(first, reflection.createConstructorAccessor(constructor))

        assertTrue(first.unregister())

        val second = reflection.createConstructorAccessor(constructor)

        assertNotSame(first, second)
        assertFalse(first.unregister())
        assertSame(second, reflection.createConstructorAccessor(constructor))

        assertEquals(42, first.newInstance(42).value)
        assertEquals(24, second.newInstance(24).value)
    }

    @Test
    fun `unregistering method accessor removes cached entry`() {
        val method =
            TestTarget::class.java.getDeclaredMethod("echo", String::class.java)

        val first = reflection.createMethodAccessor(method)

        assertSame(first, reflection.createMethodAccessor(method))

        assertTrue(first.unregister())

        val second = reflection.createMethodAccessor(method)

        assertNotSame(first, second)
        assertFalse(first.unregister())
        assertSame(second, reflection.createMethodAccessor(method))

        assertEquals("first", first.invoke(TestTarget(1), "first"))
        assertEquals("second", second.invoke(TestTarget(1), "second"))
    }

    @Test
    fun `unregistering field accessor removes cached entry`() {
        val field = TestTarget::class.java.getDeclaredField("value")
        val first = reflection.createFieldAccessor(field)

        assertSame(first, reflection.createFieldAccessor(field))

        assertTrue(first.unregister())

        val second = reflection.createFieldAccessor(field)

        assertNotSame(first, second)
        assertFalse(first.unregister())
        assertSame(second, reflection.createFieldAccessor(field))

        val target = TestTarget(10)

        first.set(target, 20)

        assertEquals(20, second.get(target))
    }

    @Test
    fun `clearing accessor cache evicts all accessors`() {
        val constructor =
            TestTarget::class.java.getConstructor(Int::class.javaPrimitiveType!!)

        val method =
            TestTarget::class.java.getDeclaredMethod("echo", String::class.java)

        val field = TestTarget::class.java.getDeclaredField("value")

        val constructorAccessor = reflection.createConstructorAccessor(constructor)
        val methodAccessor = reflection.createMethodAccessor(method)
        val fieldAccessor = reflection.createFieldAccessor(field)

        reflection.clearAccessorCache()

        assertNotSame(constructorAccessor, reflection.createConstructorAccessor(constructor))
        assertNotSame(methodAccessor, reflection.createMethodAccessor(method))
        assertNotSame(fieldAccessor, reflection.createFieldAccessor(field))

        // Existing accessor instances must remain usable after clearing the cache.
        assertEquals(42, constructorAccessor.newInstance(42).value)
        assertEquals("still usable", methodAccessor.invoke(TestTarget(1), "still usable"))

        val target = TestTarget(1)

        fieldAccessor.set(target, 99)

        assertEquals(99, target.value)
    }
}
