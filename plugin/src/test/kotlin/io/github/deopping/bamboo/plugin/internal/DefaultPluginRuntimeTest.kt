package io.github.deopping.bamboo.plugin.internal

import io.github.deopping.bamboo.plugin.api.BambooPlugin
import io.github.deopping.bamboo.plugin.api.PluginContext
import io.github.deopping.bamboo.plugin.api.PluginMeta
import io.github.deopping.bamboo.plugin.api.PluginRuntimeFactory
import io.github.deopping.bamboo.plugin.api.PluginState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.util.logging.Logger

class DefaultPluginRuntimeTest {
    private val meta =
        PluginMeta(
            id = "test",
            name = "Test Plugin",
            version = "1.0.0",
            apiVersion = "26.3",
        )

    private val context =
        object : PluginContext {
            override val meta: PluginMeta
                get() = this@DefaultPluginRuntimeTest.meta

            override val dataDirectory: Path = Path.of("test")

            override val logger: Logger = Logger.getLogger("test")
        }

    @Test
    fun `initial state is unloaded`() {
        val plugin = TestPlugin(meta)
        val runtime = DefaultPluginRuntime(plugin, context)

        assertEquals(PluginState.UNLOADED, runtime.state)
        assertTrue(runtime.failure.isEmpty)
    }

    @Test
    fun `load transitions to loaded`() {
        val plugin = TestPlugin(meta)
        val runtime = DefaultPluginRuntime(plugin, context)

        runtime.load()

        assertEquals(PluginState.LOADED, runtime.state)
        assertEquals(1, plugin.loadCalls)
    }

    @Test
    fun `enable transitions to enabled`() {
        val plugin = TestPlugin(meta)
        val runtime = DefaultPluginRuntime(plugin, context)

        runtime.load()
        runtime.enable()

        assertEquals(PluginState.ENABLED, runtime.state)
        assertEquals(1, plugin.enableCalls)
    }

    @Test
    fun `disable transitions to disabled`() {
        val plugin = TestPlugin(meta)
        val runtime = DefaultPluginRuntime(plugin, context)

        runtime.load()
        runtime.enable()
        runtime.disable()

        assertEquals(PluginState.DISABLED, runtime.state)
        assertEquals(1, plugin.disableCalls)
    }

    @Test
    fun `load failure transitions to failed`() {
        val exception = IllegalStateException("Load failed")

        val plugin =
            TestPlugin(
                meta,
                loadAction = { throw exception },
            )

        val runtime = DefaultPluginRuntime(plugin, context)

        val thrown =
            assertThrows(IllegalStateException::class.java) {
                runtime.load()
            }

        assertSame(exception, thrown)
        assertEquals(PluginState.FAILED, runtime.state)
        assertTrue(runtime.failure.isPresent)
        assertSame(exception, runtime.failure.get())
    }

    @Test
    fun `enable failure transitions to failed`() {
        val exception = IllegalStateException("Enable failed")

        val plugin =
            TestPlugin(
                meta,
                enableAction = { throw exception },
            )

        val runtime = DefaultPluginRuntime(plugin, context)

        runtime.load()

        val thrown =
            assertThrows(IllegalStateException::class.java) {
                runtime.enable()
            }

        assertSame(exception, thrown)
        assertEquals(PluginState.FAILED, runtime.state)
        assertTrue(runtime.failure.isPresent)
        assertSame(exception, runtime.failure.get())
    }

    @Test
    fun `disable failure transitions to failed`() {
        val exception = IllegalStateException("Disable failed")

        val plugin =
            TestPlugin(
                meta,
                disableAction = { throw exception },
            )

        val runtime = DefaultPluginRuntime(plugin, context)

        runtime.load()
        runtime.enable()

        val thrown =
            assertThrows(IllegalStateException::class.java) {
                runtime.disable()
            }

        assertSame(exception, thrown)
        assertEquals(PluginState.FAILED, runtime.state)
        assertTrue(runtime.failure.isPresent)
        assertSame(exception, runtime.failure.get())
    }

    @Test
    fun `failed runtime cannot be enabled`() {
        val plugin =
            TestPlugin(
                meta,
                loadAction = { throw IllegalStateException("Load failed") },
            )

        val runtime = DefaultPluginRuntime(plugin, context)

        assertThrows(IllegalStateException::class.java) {
            runtime.load()
        }

        assertThrows(IllegalStateException::class.java) {
            runtime.enable()
        }

        assertEquals(PluginState.FAILED, runtime.state)
    }

    @Test
    fun `cannot load twice`() {
        val plugin = TestPlugin(meta)
        val runtime = DefaultPluginRuntime(plugin, context)

        runtime.load()

        assertThrows(IllegalStateException::class.java) {
            runtime.load()
        }

        assertEquals(1, plugin.loadCalls)
    }

    @Test
    fun `cannot enable twice`() {
        val plugin = TestPlugin(meta)
        val runtime = DefaultPluginRuntime(plugin, context)

        runtime.load()
        runtime.enable()

        assertThrows(IllegalStateException::class.java) {
            runtime.enable()
        }

        assertEquals(1, plugin.enableCalls)
    }

    @Test
    fun `cannot disable twice`() {
        val plugin = TestPlugin(meta)
        val runtime = DefaultPluginRuntime(plugin, context)

        runtime.load()
        runtime.enable()
        runtime.disable()

        assertThrows(IllegalStateException::class.java) {
            runtime.disable()
        }

        assertEquals(1, plugin.disableCalls)
    }

    @Test
    fun `factory creates runtime when metadata matches`() {
        val plugin = TestPlugin(meta)

        val runtime = PluginRuntimeFactory.create(plugin, context)

        assertEquals(PluginState.UNLOADED, runtime.state)
        assertSame(plugin, runtime.plugin)
        assertSame(context, runtime.context)
    }

    @Test
    fun `factory rejects mismatched metadata`() {
        val plugin = TestPlugin(meta)

        val mismatchedContext =
            object : PluginContext {
                override val meta =
                    PluginMeta(
                        id = "different",
                        name = "Different Plugin",
                        version = "1.0.0",
                        apiVersion = "26.3",
                    )

                override val dataDirectory: Path = Path.of("test")
                override val logger: Logger = Logger.getLogger("test")
            }

        assertThrows(IllegalArgumentException::class.java) {
            PluginRuntimeFactory.create(plugin, mismatchedContext)
        }
    }

    @Test
    fun `original failure is preserved after subsequent invalid operation`() {
        val exception = IllegalStateException("Load failed")

        val plugin =
            TestPlugin(
                meta,
                loadAction = { throw exception },
            )

        val runtime = DefaultPluginRuntime(plugin, context)

        assertThrows(IllegalStateException::class.java) {
            runtime.load()
        }

        assertThrows(IllegalStateException::class.java) {
            runtime.enable()
        }

        assertEquals(PluginState.FAILED, runtime.state)
        assertTrue(runtime.failure.isPresent)
        assertSame(exception, runtime.failure.get())
    }

    @Test
    fun `callbacks execute in lifecycle order`() {
        val callbacks = mutableListOf<String>()

        val plugin =
            TestPlugin(
                meta,
                loadAction = { callbacks.add("load") },
                enableAction = { callbacks.add("enable") },
                disableAction = { callbacks.add("disable") },
            )

        val runtime = DefaultPluginRuntime(plugin, context)

        runtime.load()
        assertSame(context, plugin.receivedContext)

        runtime.enable()
        assertSame(context, plugin.receivedContext)

        runtime.disable()
        assertSame(context, plugin.receivedContext)

        assertEquals(
            listOf("load", "enable", "disable"),
            callbacks,
        )

        assertEquals(PluginState.DISABLED, runtime.state)
    }

    private class TestPlugin(
        override val meta: PluginMeta,
        private val loadAction: () -> Unit = {},
        private val enableAction: () -> Unit = {},
        private val disableAction: () -> Unit = {},
    ) : BambooPlugin {
        var loadCalls: Int = 0
            private set

        var enableCalls: Int = 0
            private set

        var disableCalls: Int = 0
            private set

        var receivedContext: PluginContext? = null
            private set

        override fun onLoad(context: PluginContext) {
            receivedContext = context
            loadCalls++
            loadAction()
        }

        override fun onEnable(context: PluginContext) {
            receivedContext = context
            enableCalls++
            enableAction()
        }

        override fun onDisable(context: PluginContext) {
            receivedContext = context
            disableCalls++
            disableAction()
        }
    }
}
