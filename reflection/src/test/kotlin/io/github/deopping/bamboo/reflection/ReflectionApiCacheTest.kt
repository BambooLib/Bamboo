package io.github.deopping.bamboo.reflection

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * @author DeOpping
 * @since 0.1.0
 */
class ReflectionApiCacheTest {

    private lateinit var reflection: ReflectionApi

    class TestTarget(var value: Int) {
        fun echo(input: String): String = input

        constructor(value: String): this(value.toInt())
    }

    @BeforeEach
    fun setup() {
        reflection = ReflectionApi.create()
    }

    @Test
    fun `remove constructor accessor removes cached entry`() {
        val constructor = TestTarget::class.java.getConstructor(Int::class.javaPrimitiveType)
        val first = reflection.createConstructorAccessor(constructor)

        assertSame(first, reflection.createConstructorAccessor(constructor))
        assertTrue(reflection.removeCachedConstructorAccessor(constructor))
        assertFalse(reflection.removeCachedConstructorAccessor(constructor))

        val second = reflection.createConstructorAccessor(constructor)

        assertNotSame(first, second)

        assertEquals(42, first.newInstance(42).value)
        assertEquals(24, second.newInstance(24).value)
    }

    @Test
    fun `remove method accessor removes cached entry`() {
        val method = TestTarget::class.java.getDeclaredMethod("echo", String::class.java)
        val first = reflection.createMethodAccessor(method)

        assertSame(first, reflection.createMethodAccessor(method))
        assertTrue(reflection.removeCachedMethodAccessor(method))
        assertFalse(reflection.removeCachedMethodAccessor(method))

        val second = reflection.createMethodAccessor(method)

        assertNotSame(first, second)
        assertEquals("first", first.invoke(TestTarget(1), "first"))
        assertEquals("second", second.invoke(TestTarget(1), "second"))
    }

    @Test
    fun `remove field accessor removes cached entry`() {
        val field = TestTarget::class.java.getDeclaredField("value")
        val first = reflection.createFieldAccessor(field)

        assertSame(first, reflection.createFieldAccessor(field))
        assertTrue(reflection.removeCachedFieldAccessor(field))
        assertFalse(reflection.removeCachedFieldAccessor(field))

        val second = reflection.createFieldAccessor(field)

        assertNotSame(first, second)

        val target = TestTarget(10)
        first.set(target, 20)
        assertEquals(20, second.get(target))
    }

    @Test
    fun `clear constructor cache preserves cached accessors`() {
        val constructor = TestTarget::class.java.getConstructor(Int::class.javaPrimitiveType)
        val method = TestTarget::class.java.getDeclaredMethod("echo", String::class.java)
        val field = TestTarget::class.java.getDeclaredField("value")

        val constructorAccessor = reflection.createConstructorAccessor(constructor)
        val methodAccessor = reflection.createMethodAccessor(method)
        val fieldAccessor = reflection.createFieldAccessor(field)

        reflection.clearConstructorAccessorCache()

        assertNotSame(constructorAccessor, reflection.createConstructorAccessor(constructor))
        assertSame(methodAccessor, reflection.createMethodAccessor(method))
        assertSame(fieldAccessor, reflection.createFieldAccessor(field))
    }

    @Test
    fun `clear method cache preserves cached accessors`() {
        val constructor = TestTarget::class.java.getConstructor(Int::class.javaPrimitiveType)
        val method = TestTarget::class.java.getDeclaredMethod("echo", String::class.java)
        val field = TestTarget::class.java.getDeclaredField("value")

        val constructorAccessor = reflection.createConstructorAccessor(constructor)
        val methodAccessor = reflection.createMethodAccessor(method)
        val fieldAccessor = reflection.createFieldAccessor(field)

        reflection.clearMethodAccessorCache()

        assertSame(constructorAccessor, reflection.createConstructorAccessor(constructor))
        assertNotSame(methodAccessor, reflection.createMethodAccessor(method))
        assertSame(fieldAccessor, reflection.createFieldAccessor(field))
    }

    @Test
    fun `clear field cache preserves cached accessors`() {
        val constructor = TestTarget::class.java.getConstructor(Int::class.javaPrimitiveType)
        val method = TestTarget::class.java.getDeclaredMethod("echo", String::class.java)
        val field = TestTarget::class.java.getDeclaredField("value")

        val constructorAccessor = reflection.createConstructorAccessor(constructor)
        val methodAccessor = reflection.createMethodAccessor(method)
        val fieldAccessor = reflection.createFieldAccessor(field)

        reflection.clearFieldAccessorCache()

        assertSame(constructorAccessor, reflection.createConstructorAccessor(constructor))
        assertSame(methodAccessor, reflection.createMethodAccessor(method))
        assertNotSame(fieldAccessor, reflection.createFieldAccessor(field))
    }

    @Test
    fun `clear all caches evicts every accessor type`() {
        val constructor = TestTarget::class.java.getConstructor(Int::class.javaPrimitiveType)
        val method = TestTarget::class.java.getDeclaredMethod("echo", String::class.java)
        val field = TestTarget::class.java.getDeclaredField("value")

        val constructorAccessor = reflection.createConstructorAccessor(constructor)
        val methodAccessor = reflection.createMethodAccessor(method)
        val fieldAccessor = reflection.createFieldAccessor(field)

        reflection.clearAccessorCaches()

        assertNotSame(constructorAccessor, reflection.createConstructorAccessor(constructor))
        assertNotSame(methodAccessor, reflection.createMethodAccessor(method))
        assertNotSame(fieldAccessor, reflection.createFieldAccessor(field))

        assertEquals(42, constructorAccessor.newInstance(42).value)
        assertEquals("still usable", methodAccessor.invoke(TestTarget(1), "still usable"))

        val target = TestTarget(1)
        fieldAccessor.set(target, 99)
        assertEquals(99, target.value)
    }

}