package io.github.deopping.bamboo.reflection

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * @author DeOpping
 * @since 0.1.0
 */
class ConstructorAccessorTest {

    private class TestTarget(val value: String, val count: Int) {
        private constructor() : this("private", 0)

        companion object {
            fun privateConstructor() =
                TestTarget::class.java.getDeclaredConstructor()
        }
    }

    private val reflection = ReflectionApi.create()

    @Test
    fun `creates instance using constructor arguments`() {
        val constructor = TestTarget::class.java.getDeclaredConstructor(
            String::class.java,
            Int::class.javaPrimitiveType
        )

        val accessor = reflection.createConstructorAccessor(constructor)
        val instance = accessor.newInstance("Bamboo", 5)

        assertEquals("Bamboo", instance.value)
        assertEquals(5, instance.count)
    }

    @Test
    fun `creates instance using private constructor`() {
        val constructor = TestTarget.privateConstructor()
        val accessor = reflection.createConstructorAccessor(constructor)

        val instance = accessor.newInstance()

        assertEquals("private", instance.value)
        assertEquals(0, instance.count)
    }

    @Test
    fun `reuses accessor for same constructor`() {
        val constructor = TestTarget::class.java.getDeclaredConstructor(
            String::class.java,
            Int::class.javaPrimitiveType
        )

        val first = reflection.createConstructorAccessor(constructor)
        val second = reflection.createConstructorAccessor(constructor)

        assertSame(first, second)
    }

}