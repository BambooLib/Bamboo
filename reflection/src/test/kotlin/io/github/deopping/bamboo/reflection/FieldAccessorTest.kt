package io.github.deopping.bamboo.reflection

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * @author DeOpping
 * @since 0.1.0
 */
class FieldAccessorTest {

    private class TestTarget {
        var value: String = "initial"

        private var secret: Int = 42

        companion object {
            @JvmStatic
            var staticValue: String = "static"

            const val CONSTANT: Int = 10
        }
    }

    private val reflection = ReflectionApi.create()

    @Test
    fun `reads and writes instance field`() {
        val field = TestTarget::class.java.getDeclaredField("value")
        val accessor = reflection.createFieldAccessor(field)
        val target = TestTarget()

        assertEquals("initial", accessor.get(target))

        accessor.set(target, "updated")

        assertEquals("updated", accessor.get(target))
    }

    @Test
    fun `reads and writes private instance field`() {
        val field = TestTarget::class.java.getDeclaredField("secret")
        val accessor = reflection.createFieldAccessor(field)
        val target = TestTarget()

        assertEquals(42, accessor.get(target))

        accessor.set(target, 99)

        assertEquals(99, accessor.get(target))
    }

    @Test
    fun `reads and writes static field`() {
        val field = TestTarget::class.java.getDeclaredField("staticValue")
        val accessor = reflection.createFieldAccessor(field)

        assertEquals("static", accessor.getStatic())

        accessor.setStatic("updated")

        assertEquals("updated", accessor.getStatic())
    }

    @Test
    fun `rejects instance access for static field`() {
        val field = TestTarget::class.java.getDeclaredField("staticValue")
        val accessor = reflection.createFieldAccessor(field)

        assertThrows<IllegalArgumentException> {
            accessor.get(TestTarget())
        }
    }

    @Test
    fun `rejects static access for instance field`() {
        val field = TestTarget::class.java.getDeclaredField("value")
        val accessor = reflection.createFieldAccessor(field)

        assertThrows<IllegalArgumentException> {
            accessor.getStatic()
        }
    }

    @Test
    fun `rejects writes to final field`() {
        val field = TestTarget::class.java.getDeclaredField("CONSTANT")
        val accessor = reflection.createFieldAccessor(field)

        assertEquals(10, accessor.getStatic())

        assertThrows<IllegalStateException> {
            accessor.setStatic(20)
        }
    }

    @Test
    fun `reuses accessor for same field`() {
        val field = TestTarget::class.java.getDeclaredField("value")

        val first = reflection.createFieldAccessor(field)
        val second = reflection.createFieldAccessor(field)

        assertSame(first, second)
    }

}