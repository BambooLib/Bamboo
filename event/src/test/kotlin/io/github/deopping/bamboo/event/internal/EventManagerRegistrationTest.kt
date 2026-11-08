package io.github.deopping.bamboo.event.internal

import io.github.deopping.bamboo.event.api.Event
import io.github.deopping.bamboo.event.api.EventHandler
import io.github.deopping.bamboo.event.api.EventListener
import io.github.deopping.bamboo.event.api.EventManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private class EventManagerRegistrationTest {
    private class TestEvent : Event

    private class TestListener(
        private val calls: MutableList<String>,
    ) : EventListener {
        @EventHandler
        fun onEvent(event: TestEvent) {
            calls.add("called")
        }
    }

    private class InvalidListener : EventListener {
        @EventHandler
        fun invalidHandler() {}
    }

    private fun createManager(): EventManager =
        EventManager.create(
            executor = { command -> command.run() },
            exceptionHandler = { _, _, exception ->
                throw AssertionError("Unexpected event handler exception.", exception)
            },
        )

    @Test
    fun `unregister removes listener from future dispatches`() {
        val manager = createManager()
        val calls: MutableList<String> = ArrayList()

        val subscription = manager.register(TestListener(calls))

        manager.callEvent(TestEvent())

        assertEquals(1, calls.size)
        assertTrue(subscription.isRegistered)
        assertTrue(subscription.unregister())
        assertFalse(subscription.isRegistered)

        manager.callEvent(TestEvent())

        assertEquals(1, calls.size)
        assertFalse(subscription.unregister())
    }

    @Test
    fun `closing subscription unregisters listener`() {
        val manager = createManager()
        val calls: MutableList<String> = ArrayList()

        val subscription = manager.register(TestListener(calls))

        subscription.close()
        manager.callEvent(TestEvent())

        assertFalse(subscription.isRegistered)
        assertEquals(0, calls.size)
    }

    @Test
    fun `rejects registering same listener twice`() {
        val manager = createManager()
        val listener = TestListener(ArrayList())

        manager.register(listener)

        assertThrows<IllegalStateException> {
            manager.register(listener)
        }
    }

    @Test
    fun `rejects listener without valid handlers`() {
        val manager = createManager()

        assertThrows<IllegalArgumentException> {
            manager.register(InvalidListener())
        }
    }
}
