package io.github.deopping.bamboo.event.internal

import io.github.deopping.bamboo.event.api.CancellableEvent
import io.github.deopping.bamboo.event.api.EventHandler
import io.github.deopping.bamboo.event.api.EventListener
import io.github.deopping.bamboo.event.api.EventManager
import io.github.deopping.bamboo.event.api.EventPriority
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private class EventManagerDispatchTest {
    private class TestEvent : CancellableEvent {
        override var isCanceled: Boolean = false
    }

    private class RecordingListener(
        private val calls: MutableList<String>,
    ) : EventListener {
        @EventHandler(priority = EventPriority.LOWEST)
        fun cancel(event: TestEvent) {
            calls.add("lowest")
            event.isCanceled = true
        }

        @EventHandler(
            priority = EventPriority.NORMAL,
            ignoreCanceled = true,
        )
        fun normal(event: TestEvent) {
            calls.add("normal")
        }

        @EventHandler(priority = EventPriority.HIGH)
        fun high(event: TestEvent) {
            calls.add("high")
        }

        @EventHandler(priority = EventPriority.MONITOR)
        fun monitor(event: TestEvent) {
            calls.add("monitor")
        }
    }

    @Test
    fun `handlers execute in priority order and respect cancellation`() {
        val calls: MutableList<String> = ArrayList()

        val manager =
            EventManager.create(
                executor = { command -> command.run() },
                exceptionHandler = { _, _, exception ->
                    throw AssertionError("Unexpected event handler exception.", exception)
                },
            )

        manager.register(RecordingListener(calls))

        val event = manager.callEvent(TestEvent())

        assertEquals(
            listOf("lowest", "high", "monitor"),
            calls,
        )

        assertEquals(true, event.isCanceled)
    }
}
