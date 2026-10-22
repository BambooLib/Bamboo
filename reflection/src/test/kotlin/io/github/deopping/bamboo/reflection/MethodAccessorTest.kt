package io.github.deopping.bamboo.reflection

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame

/**
 * @author DeOpping
 * @since 0.1.0
 */
class MethodAccessorTest {

    private class TestTarget {
        fun greet(name: String): String = "Hello, $name!"

        private fun secret(): String = "secret"

        fun doNothing() {
            // Intentionally empty
        }

        companion object {
            @JvmStatic
            fun add(a: Int, b: Int): Int = a + b

            @JvmStatic
            fun doNothingStatic() {}
        }
    }

    private val reflection = ReflectionApi.create()

    @Test
    fun `invokes public instance method`() {
        val target = TestTarget()
        val method = TestTarget::class.java
            .getDeclaredMethod("greet", String::class.java)

        val accessor = reflection.createMethodAccessor(method)

        assertEquals("Hello, Bamboo!", accessor.invoke(target, "Bamboo"))
    }

    @Test
    fun `invokes private instance method`() {
        val target = TestTarget()
        val method = TestTarget::class.java
            .getDeclaredMethod("secret")

        val accessor = reflection.createMethodAccessor(method)

        assertEquals("secret", accessor.invoke(target))
    }

    @Test
    fun `returns null for void method`() {
        val target = TestTarget()
        val method = TestTarget::class.java
            .getDeclaredMethod("doNothing")

        val accessor = reflection.createMethodAccessor(method)

        assertNull(accessor.invoke(target))
    }

    @Test
    fun `creates accessor for static method`() {
        val method = TestTarget::class.java
            .getDeclaredMethod(
                "add",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )

        val accessor = reflection.createMethodAccessor(method)

        assertEquals(5, accessor.invokeStatic(2, 3))
    }

    @Test
    fun `calling invoke on a static method throws illegal argument exception`() {
        val method = TestTarget::class.java
            .getDeclaredMethod(
                "add",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )

        val accessor = reflection.createMethodAccessor(method)

        assertThrows<IllegalArgumentException> {
            // Needs an instance passed in, but should still throw
            accessor.invoke(TestTarget(), 2, 5)
        }
    }

    @Test
    fun `calling invokeStatic on an instance method throws illegal argument exception`() {
        val method = TestTarget::class.java
            .getDeclaredMethod("secret")

        val accessor = reflection.createMethodAccessor(method)

        assertThrows<IllegalArgumentException> {
            accessor.invokeStatic()
        }
    }

    @Test
    fun `reuses accessor for same method`() {
        val method = TestTarget::class.java.getDeclaredMethod(
            "doNothingStatic"
        )

        val first = reflection.createMethodAccessor(method)
        val second = reflection.createMethodAccessor(method)

        assertSame(first, second)
    }

    @Test
    fun `creates separate accessors for different methods`() {
        val add = TestTarget::class.java.getDeclaredMethod(
            "add",
            Int::class.javaPrimitiveType,
            Int::class.javaPrimitiveType
        )

        val doNothingStatic = TestTarget::class.java.getDeclaredMethod(
            "doNothingStatic"
        )

        val first = reflection.createMethodAccessor(add)
        val second = reflection.createMethodAccessor(doNothingStatic)

        assertNotSame(first, second)
    }

}