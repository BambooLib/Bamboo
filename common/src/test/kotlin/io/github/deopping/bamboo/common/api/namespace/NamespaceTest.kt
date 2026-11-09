package io.github.deopping.bamboo.common.api.namespace

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private class NamespaceTest {
    @Test
    fun `constructs with valid namespace names`() {
        val validNames =
            listOf(
                "minecraft",
                "bamboo",
                "my_plugin",
                "my-plugin",
                "my.plugin",
                "plugin123",
                "123plugin",
            )

        validNames.forEach { name ->
            val namespace = Namespace(name)

            assertEquals(name, namespace.name)
        }
    }

    @Test
    fun `rejects empty namespace names`() {
        assertThrows<IllegalArgumentException> {
            Namespace("")
        }
    }

    @Test
    fun `rejects uppercase characters`() {
        val invalidNames =
            listOf(
                "Minecraft",
                "BAMBOO",
                "myPlugin",
                "My_Plugin",
            )

        invalidNames.forEach { name ->
            assertThrows<IllegalArgumentException> {
                Namespace(name)
            }
        }
    }

    @Test
    fun `rejects unsupported characters`() {
        val invalidNames =
            listOf(
                "my plugin",
                "my/plugin",
                "my:plugin",
                "my@plugin",
                "my#plugin",
                "my\\plugin",
            )

        invalidNames.forEach { name ->
            assertThrows<IllegalArgumentException> {
                Namespace(name)
            }
        }
    }

    @Test
    fun `parse returns namespace for valid names`() {
        val namespace = Namespace.parse("minecraft")

        assertTrue(namespace.isPresent)
        assertEquals(Namespace("minecraft"), namespace.get())
    }

    @Test
    fun `parse returns empty optional for invalid names`() {
        val invalidNames =
            listOf(
                "",
                "Minecraft",
                "my plugin",
                "my/plugin",
                "my:plugin",
            )

        invalidNames.forEach { name ->
            assertTrue(
                Namespace.parse(name).isEmpty,
                "Expected '$name' to be rejected.",
            )
        }
    }

    @Test
    fun `equal namespaces have equal hash codes`() {
        val first = Namespace("minecraft")
        val second = Namespace("minecraft")

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun `different namespaces are not equal`() {
        val first = Namespace("minecraft")
        val second = Namespace("bamboo")

        assertNotEquals(first, second)
    }

    @Test
    fun `toString returns namespace name`() {
        val namespace = Namespace("minecraft")

        assertEquals("minecraft", namespace.toString())
    }
}
