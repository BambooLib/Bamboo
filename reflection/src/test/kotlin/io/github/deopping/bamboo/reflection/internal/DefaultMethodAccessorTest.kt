package io.github.deopping.bamboo.reflection.internal

import io.github.deopping.bamboo.reflection.ReflectionApi
import io.github.deopping.bamboo.reflection.api.MethodAccessor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.ConcurrentHashMap

/**
 * @author DeOpping
 * @since 1.0.0
 */
private class DefaultMethodAccessorTest {
    private class TestTarget {
        fun greet(name: String): String = "Hello, $name!"

        private fun secret(): String = "secret"

        fun doNothing() {
            // Intentionally empty
        }

        private fun throwingMethod(): Nothing {
            throw IllegalStateException("boom")
        }

        private fun echo(message: String) {}

        companion object {
            @JvmStatic
            fun add(
                a: Int,
                b: Int,
            ): Int = a + b

            @JvmStatic
            fun doNothingStatic() {}
        }
    }

    private val reflection = ReflectionApi.create()

    @Test
    fun `invokes public instance method`() {
        val target = TestTarget()
        val method =
            TestTarget::class.java
                .getDeclaredMethod("greet", String::class.java)

        val accessor = reflection.createMethodAccessor(method)

        assertEquals("Hello, Bamboo!", accessor.invoke(target, "Bamboo"))
    }

    @Test
    fun `invokes private instance method`() {
        val target = TestTarget()
        val method =
            TestTarget::class.java
                .getDeclaredMethod("secret")

        val accessor = reflection.createMethodAccessor(method)

        assertEquals("secret", accessor.invoke(target))
    }

    @Test
    fun `returns null for void method`() {
        val target = TestTarget()
        val method =
            TestTarget::class.java
                .getDeclaredMethod("doNothing")

        val accessor = reflection.createMethodAccessor(method)

        assertNull(accessor.invoke(target))
    }

    @Test
    fun `creates accessor for static method`() {
        val method =
            TestTarget::class.java
                .getDeclaredMethod(
                    "add",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                )

        val accessor = reflection.createMethodAccessor(method)

        assertEquals(5, accessor.invokeStatic(2, 3))
    }

    @Test
    fun `calling invoke on a static method throws illegal argument exception`() {
        val method =
            TestTarget::class.java
                .getDeclaredMethod(
                    "add",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                )

        val accessor = reflection.createMethodAccessor(method)

        assertThrows<IllegalArgumentException> {
            // Needs an instance passed in, but should still throw
            accessor.invoke(TestTarget(), 2, 5)
        }
    }

    @Test
    fun `calling invokeStatic on an instance method throws illegal argument exception`() {
        val method =
            TestTarget::class.java
                .getDeclaredMethod("secret")

        val accessor = reflection.createMethodAccessor(method)

        assertThrows<IllegalArgumentException> {
            accessor.invokeStatic()
        }
    }

    @Test
    fun `reuses accessor for same method`() {
        val method =
            TestTarget::class.java.getDeclaredMethod(
                "doNothingStatic",
            )

        val first = reflection.createMethodAccessor(method)
        val second = reflection.createMethodAccessor(method)

        assertSame(first, second)
    }

    @Test
    fun `creates separate accessors for different methods`() {
        val add =
            TestTarget::class.java.getDeclaredMethod(
                "add",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
            )

        val doNothingStatic =
            TestTarget::class.java.getDeclaredMethod(
                "doNothingStatic",
            )

        val first = reflection.createMethodAccessor(add)
        val second = reflection.createMethodAccessor(doNothingStatic)

        assertNotSame(first, second)
    }

    @Test
    fun `reports instance method capabilities`() {
        val method = TestTarget::class.java.getDeclaredMethod("greet", String::class.java)
        val accessor = reflection.createMethodAccessor(method)

        assertFalse(accessor.isStatic)
    }

    @Test
    fun `reports static method capabilities`() {
        val method =
            TestTarget::class.java.getDeclaredMethod(
                "add",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
            )

        val accessor = reflection.createMethodAccessor(method)

        assertTrue(accessor.isStatic)
    }

    @Test
    fun `rejects incorrect argument count`() {
        val method =
            TestTarget::class.java.getDeclaredMethod(
                "greet",
                String::class.java,
            )

        val accessor = reflection.createMethodAccessor(method)

        assertThrows<IllegalArgumentException> {
            accessor.invoke(TestTarget())
        }
    }

    @Test
    fun `propagates exception thrown by invoked method`() {
        val method = TestTarget::class.java.getDeclaredMethod("throwingMethod")
        val accessor = reflection.createMethodAccessor(method)

        val exception =
            assertThrows<IllegalStateException> {
                accessor.invoke(TestTarget())
            }

        assertEquals("boom", exception.message)
    }

    @Test
    fun `concurrent accessor creation returns the same cached accessor`() {
        val method =
            TestTarget::class.java.getDeclaredMethod(
                "echo",
                String::class.java,
            )

        val accessors = ConcurrentHashMap.newKeySet<MethodAccessor>()

        val threads =
            List(16) {
                Thread {
                    accessors += reflection.createMethodAccessor(method)
                }
            }

        threads.forEach(Thread::start)
        threads.forEach(Thread::join)

        assertEquals(1, accessors.size)
    }
}
