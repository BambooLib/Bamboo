package io.github.deopping.bamboo.actions.internal

import io.github.deopping.bamboo.actions.api.Action
import io.github.deopping.bamboo.actions.api.ActionConfig
import io.github.deopping.bamboo.actions.api.ActionException
import io.github.deopping.bamboo.actions.api.ActionFactory
import io.github.deopping.bamboo.actions.api.ActionType
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ActionRegistryImplTest {

    private val registry = ActionRegistryImpl<TestContext>()

    private val messageType = ActionType("test", "message")

    private val otherType = ActionType("test", "other")

    private class TestContext {
        val messages = mutableListOf<String>()
    }

    private data class MessageConfig(
        val message: String
    ) : ActionConfig

    private data class OtherConfig(
        val value: String
    ) : ActionConfig

    /**
     * Creates a factory that appends its configured message
     * to the execution context.
     */
    private fun messageFactory(): ActionFactory<TestContext, MessageConfig> =
        ActionFactory { configuration ->
            Action { context ->
                context.messages.add(configuration.message)
            }
        }

    @Test
    fun `registers an action type`() {
        val registration = registry.register(
            messageType,
            MessageConfig::class,
            messageFactory()
        )

        assertTrue(registry.contains(messageType))
        assertTrue(registration.isRegistered)
        assertEquals(setOf(messageType), registry.registeredTypes)
    }

    @Test
    fun `creates and executes a registered action`() {
        registry.register(
            messageType,
            MessageConfig::class,
            messageFactory()
        )

        val action = registry.create(
            messageType,
            MessageConfig("Hello, world!")
        )

        val context = TestContext()

        action.execute(context)

        assertEquals(
            listOf("Hello, world!"),
            context.messages
        )
    }

    @Test
    fun `rejects duplicate registrations`() {
        registry.register(
            messageType,
            MessageConfig::class,
            messageFactory()
        )

        assertFailsWith<ActionException> {
            registry.register(
                messageType,
                MessageConfig::class,
                messageFactory()
            )
        }

        // The original registration must remain intact
        assertTrue(registry.contains(messageType))
        assertEquals(setOf(messageType), registry.registeredTypes)
    }

    @Test
    fun `rejects an incompatible configuration`() {
        registry.register(
            messageType,
            MessageConfig::class,
            messageFactory()
        )

        assertFailsWith<ActionException> {
            registry.create(messageType,
                OtherConfig("Wrong configuration")
            )
        }
    }

    @Test
    fun `does not invoke factory for incompatible configuration`() {
        var factoryCalls = 0

        registry.register(
            messageType,
            MessageConfig::class,
            ActionFactory { configuration ->
                factoryCalls++

                Action { context ->
                    context.messages.add(configuration.message)
                }
            }
        )

        assertFailsWith<ActionException> {
            registry.create(
                messageType,
                OtherConfig("Wrong configuration")
            )
        }

        assertEquals(0, factoryCalls)
    }

    @Test
    fun `rejects an unknown action type`() {
        assertFailsWith<ActionException> {
            registry.create(
                otherType,
                MessageConfig("Hello")
            )
        }
    }

    @Test
    fun `unregisters an action`() {
        val registration = registry.register(
            messageType,
            MessageConfig::class,
            messageFactory()
        )

        registration.unregister()

        assertFalse(registry.contains(messageType))
        assertFalse(registration.isRegistered)
        assertTrue(registry.registeredTypes.isEmpty())

        assertFailsWith<ActionException> {
            registry.create(
                messageType,
                MessageConfig("Hello")
            )
        }
    }

    @Test
    fun `closing registration unregisters action`() {
        val registration = registry.register(
            messageType,
            MessageConfig::class,
            messageFactory()
        )

        registration.close()

        assertFalse(registration.isRegistered)
        assertFalse(registry.contains(messageType))
    }

    @Test
    fun `unregistering twice is safe`() {
        val registration = registry.register(
            messageType,
            MessageConfig::class,
            messageFactory()
        )

        registration.unregister()
        registration.unregister()

        assertFalse(registration.isRegistered)
        assertFalse(registry.contains(messageType))
    }

    @Test
    fun `old registration cannot remove a newer registration`() {
        val oldRegistration = registry.register(
            messageType,
            MessageConfig::class,
            messageFactory()
        )

        oldRegistration.unregister()

        val newRegistration = registry.register(
            messageType,
            MessageConfig::class,
            messageFactory()
        )

        // Calling the old handle again must not affect the new entry.
        oldRegistration.unregister()

        assertFalse(oldRegistration.isRegistered)
        assertTrue(newRegistration.isRegistered)
        assertTrue(registry.contains(messageType))

        val context = TestContext()

        registry.create(
            messageType,
            MessageConfig("Still registered")
        ).execute(context)

        assertEquals(listOf("Still registered"), context.messages)
    }

    @Test
    fun `unregistering one type leaves other types registered`() {
        val messageRegistration = registry.register(
            messageType,
            MessageConfig::class,
            messageFactory()
        )

        registry.register(
            otherType,
            OtherConfig::class,
            ActionFactory { configuration ->
                Action { context ->
                    context.messages.add(configuration.value)
                }
            }
        )

        messageRegistration.unregister()

        assertFalse(registry.contains(messageType))
        assertTrue(registry.contains(otherType))
        assertEquals(setOf(otherType), registry.registeredTypes)

        val context = TestContext()

        registry.create(
            otherType,
            OtherConfig("Still available")
        ).execute(context)

        assertEquals(listOf("Still available"), context.messages)
    }

    @Test
    fun `wraps factory failure with action type and cause`() {
        val cause = IllegalStateException("Factory failed")

        registry.register(
            messageType,
            MessageConfig::class,
            ActionFactory { throw cause }
        )

        val exception = assertFailsWith<ActionException> {
            registry.create(
                messageType,
                MessageConfig("Hello")
            )
        }

        assertEquals(
            "Failed to create action `$messageType`.",
            exception.message
        )

        assertEquals(cause, exception.cause)
    }

}