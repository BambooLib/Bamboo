package io.github.deopping.bamboo.event.internal

import io.github.deopping.bamboo.event.api.Event
import io.github.deopping.bamboo.event.api.EventHandler
import io.github.deopping.bamboo.event.api.EventListener
import io.github.deopping.bamboo.event.api.EventManager
import io.github.deopping.bamboo.event.api.EventPriority
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private class EventManagerConcurrencyTest {
    private class TestEvent : Event

    @Test
    fun `different events can be dispatched concurrently`() {
        val executor = Executors.newFixedThreadPool(2)

        val entered = CountDownLatch(2)
        val release = CountDownLatch(1)

        val manager =
            EventManager.create(
                executor = executor,
                exceptionHandler = { _, _, exception ->
                    throw AssertionError("Unexpected event handler exception.", exception)
                },
            )

        val listener =
            object : EventListener {
                @EventHandler
                fun onEvent(event: TestEvent) {
                    entered.countDown()
                    check(release.await(5, TimeUnit.SECONDS)) {
                        "Timed out waiting for the test to release event handlers."
                    }
                }
            }

        try {
            manager.register(listener)

            val first = manager.callEventAsync(TestEvent())
            val second = manager.callEventAsync(TestEvent())

            assertTrue(
                entered.await(2, TimeUnit.SECONDS),
                "Expected both event handlers to be running concurrently",
            )

            release.countDown()

            first.join()
            second.join()
        } finally {
            release.countDown()
            executor.shutdownNow()
        }
    }

    @Test
    fun `unregister does not interrupt an active dispatch`() {
        val executor = Executors.newSingleThreadExecutor()

        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        val laterCalls = AtomicInteger()

        val manager =
            EventManager.create(
                executor = executor,
                exceptionHandler = { _, _, exception ->
                    throw AssertionError("Unexpected event handler exception.", exception)
                },
            )

        val listener =
            object : EventListener {
                @EventHandler(priority = EventPriority.LOWEST)
                fun first(event: TestEvent) {
                    entered.countDown()
                    check(release.await(5, TimeUnit.SECONDS))
                }

                @EventHandler(priority = EventPriority.HIGH)
                fun second(event: TestEvent) {
                    laterCalls.incrementAndGet()
                }
            }

        try {
            val subscription = manager.register(listener)
            val dispatch = manager.callEventAsync(TestEvent())

            assertTrue(entered.await(2, TimeUnit.SECONDS))
            assertTrue(subscription.unregister())

            release.countDown()
            dispatch.join()

            assertEquals(1, laterCalls.get())

            manager.callEvent(TestEvent())

            assertEquals(1, laterCalls.get())
        } finally {
            release.countDown()
            executor.shutdownNow()
        }
    }
}
