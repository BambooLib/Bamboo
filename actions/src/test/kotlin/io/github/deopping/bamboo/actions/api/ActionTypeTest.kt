package io.github.deopping.bamboo.actions.api

import io.github.deopping.bamboo.common.api.NamespacedKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ActionTypeTest {

    @Test
    fun `creates action type from namespace and name`() {
        val type = ActionType("bamboo", "message")

        assertEquals("bamboo:message", type.toString())
    }

    @Test
    fun `creates action type from namespaced key`() {
        val key = NamespacedKey("bamboo", "message")
        val type = ActionType(key)

        assertEquals(key, type.namespacedKey)
        assertEquals("bamboo:message", type.toString())
    }

    @Test
    fun `rejects invalid namespace`() {
        assertFailsWith<IllegalArgumentException> {
            ActionType("Invalid Namespace", "message")
        }
    }

    @Test
    fun `action types created through different constructors are equal`() {
        val fromComponents = ActionType("bamboo", "message")
        val fromKey = ActionType(
            NamespacedKey("bamboo", "message")
        )

        Assertions.assertEquals(fromComponents, fromKey)
        Assertions.assertEquals(fromComponents.hashCode(), fromKey.hashCode())
    }

}