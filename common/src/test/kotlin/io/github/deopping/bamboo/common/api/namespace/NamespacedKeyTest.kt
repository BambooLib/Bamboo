package io.github.deopping.bamboo.common.api.namespace

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private class NamespacedKeyTest {
    @Test
    fun `constructs with valid namespace and resource keys`() {
        val validKeys =
            listOf(
                "zombie",
                "custom_entity",
                "custom-entity",
                "custom.entity",
                "entity123",
                "entities/custom_boss",
                "entities/bosses/custom_boss",
                "123entity",
            )

        validKeys.forEach { key ->
            val namespacedKey =
                NamespacedKey(
                    Namespace("minecraft"),
                    key,
                )

            assertEquals(Namespace("minecraft"), namespacedKey.namespace)
            assertEquals(key, namespacedKey.key)
        }
    }

    @Test
    fun `rejects empty resource keys`() {
        assertThrows<IllegalArgumentException> {
            NamespacedKey(Namespace("minecraft"), "")
        }
    }

    @Test
    fun `rejects uppercase characters`() {
        val invalidKeys =
            listOf(
                "Zombie",
                "CUSTOM_ENTITY",
                "customEntity",
                "Entities/Custom_Boss",
            )

        invalidKeys.forEach { key ->
            assertThrows<IllegalArgumentException> {
                NamespacedKey(Namespace("minecraft"), key)
            }
        }
    }

    @Test
    fun `rejects unsupported resource key characters`() {
        val invalidKeys =
            listOf(
                "my entity",
                "my:entity",
                "my@entity",
                "my#entity",
                "my\\entity",
                "my?entity",
            )

        invalidKeys.forEach { key ->
            assertThrows<IllegalArgumentException> {
                NamespacedKey(Namespace("minecraft"), key)
            }
        }
    }

    @Test
    fun `parse returns namespaced key for valid identifiers`() {
        val namespacedKey = NamespacedKey.parse("minecraft:zombie")

        assertTrue(namespacedKey.isPresent)
        assertEquals(
            NamespacedKey(Namespace("minecraft"), "zombie"),
            namespacedKey.get(),
        )
    }

    @Test
    fun `parse supports hierarchical resource keys`() {
        val namespacedKey =
            NamespacedKey.parse(
                "bamboo:entities/custom_boss",
            )

        assertTrue(namespacedKey.isPresent)
        assertEquals(
            NamespacedKey(
                Namespace("bamboo"),
                "entities/custom_boss",
            ),
            namespacedKey.get(),
        )
    }

    @Test
    fun `parse returns empty optional for missing namespace`() {
        val namespacedKey = NamespacedKey.parse("zombie")

        assertTrue(namespacedKey.isEmpty)
    }

    @Test
    fun `parse returns empty optional for empty namespace`() {
        val namespacedKey = NamespacedKey.parse(":zombie")

        assertTrue(namespacedKey.isEmpty)
    }

    @Test
    fun `parse returns empty optional for empty resource key`() {
        val namespacedKey = NamespacedKey.parse("minecraft:")

        assertTrue(namespacedKey.isEmpty)
    }

    @Test
    fun `parse rejects multiple namespace separators`() {
        val namespacedKey = NamespacedKey.parse("minecraft:entities:zombie")

        assertTrue(namespacedKey.isEmpty)
    }

    @Test
    fun `parse rejects invalid namespace`() {
        val namespacedKey = NamespacedKey.parse("Minecraft:zombie")

        assertTrue(namespacedKey.isEmpty)
    }

    @Test
    fun `parse rejects invalid resource key`() {
        val invalidKeys =
            listOf(
                "minecraft:Zombie",
                "minecraft:my entity",
                "minecraft:my@entity",
                "minecraft:my#entity",
            )

        invalidKeys.forEach { value ->
            assertTrue(
                NamespacedKey.parse(value).isEmpty,
                "Expected '$value' to be rejected.",
            )
        }
    }

    @Test
    fun `equal namespaced keys have equal hash codes`() {
        val first =
            NamespacedKey(
                Namespace("minecraft"),
                "zombie",
            )

        val second =
            NamespacedKey(
                Namespace("minecraft"),
                "zombie",
            )

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
        assertEquals(first, first.copy())
    }

    @Test
    fun `different namespaces produce different keys`() {
        val first =
            NamespacedKey(
                Namespace("minecraft"),
                "zombie",
            )

        val second =
            NamespacedKey(
                Namespace("bamboo"),
                "zombie",
            )

        assertNotEquals(first, second)
    }

    @Test
    fun `different resource keys produce different keys`() {
        val first =
            NamespacedKey(
                Namespace("minecraft"),
                "zombie",
            )

        val second =
            NamespacedKey(
                Namespace("minecraft"),
                "skeleton",
            )

        assertNotEquals(first, second)
    }

    @Test
    fun `toString returns fully qualified identifier`() {
        val namespacedKey =
            NamespacedKey(
                Namespace("minecraft"),
                "entities/zombie",
            )

        assertEquals(
            "minecraft:entities/zombie",
            namespacedKey.toString(),
        )
    }

    @Test
    fun `parsed identifier can be converted back to its original string`() {
        val identifiers =
            listOf(
                "minecraft:zombie",
                "minecraft:entities/ender_dragon",
                "bamboo:custom/bosses/final_boss",
                "my-plugin:custom_entity",
                "my.plugin:some.resource",
            )

        identifiers.forEach { identifier ->
            val parsed = NamespacedKey.parse(identifier)

            assertTrue(parsed.isPresent)
            assertEquals(identifier, parsed.get().toString())
        }
    }

    @Test
    fun `copy preserves unchanged properties`() {
        val original =
            NamespacedKey(
                Namespace("minecraft"),
                "zombie",
            )

        val copy = original.copy()

        assertEquals(original, copy)
        assertEquals(original.hashCode(), copy.hashCode())
    }

    @Test
    fun `copy can change resource key`() {
        val original =
            NamespacedKey(
                Namespace("minecraft"),
                "zombie",
            )

        val copy = original.copy(key = "skeleton")

        assertEquals(Namespace("minecraft"), copy.namespace)
        assertEquals("skeleton", copy.key)
        assertNotEquals(original, copy)
    }

    @Test
    fun `copy validates changed resource key`() {
        val original =
            NamespacedKey(
                Namespace("minecraft"),
                "zombie",
            )

        assertThrows<IllegalArgumentException> {
            original.copy(key = "Invalid:key")
        }
    }

    @Test
    fun `of creates namespaced key from strings`() {
        val namespacedKey = NamespacedKey.of("minecraft", "zombie")

        assertEquals(
            NamespacedKey(Namespace("minecraft"), "zombie"),
            namespacedKey,
        )
    }

    @Test
    fun `of supports hierarchical resource keys`() {
        val namespacedKey =
            NamespacedKey.of(
                "bamboo",
                "entities/custom_boss",
            )

        assertEquals(Namespace("bamboo"), namespacedKey.namespace)
        assertEquals("entities/custom_boss", namespacedKey.key)
    }

    @Test
    fun `of rejects invalid namespaces`() {
        assertThrows<IllegalArgumentException> {
            NamespacedKey.of("Minecraft", "zombie")
        }
    }

    @Test
    fun `of rejects invalid resource keys`() {
        assertThrows<IllegalArgumentException> {
            NamespacedKey.of("minecraft", "Invalid:key")
        }
    }
}
