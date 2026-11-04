package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.ConfigResource
import io.github.deopping.bamboo.configuration.api.exceptions.ConfigException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

private class DefaultConfigResourceTest {
    @Test
    fun `opens a custom resource`() {
        val content = "enabled = true"

        val resource =
            ConfigResource {
                ByteArrayInputStream(
                    content.toByteArray(StandardCharsets.UTF_8),
                )
            }

        val loaded =
            resource.open().use { input ->
                input.readBytes().toString(StandardCharsets.UTF_8)
            }

        assertEquals(content, loaded)
    }

    @Test
    fun `rejects a blank classpath path`() {
        assertThrows(IllegalArgumentException::class.java) {
            ConfigResource.classpath(" ")
        }
    }

    @Test
    fun `throws when a classpath resource does not exist`() {
        val resource =
            ConfigResource.classpath(
                "missing/dropdeck-test-resource.toml",
                javaClass.classLoader,
            )

        assertThrows(ConfigException::class.java) {
            resource.open()
        }
    }

    @Test
    fun `strips the leading slash from classpath paths`() {
        val content = "enabled = true"

        val classLoader =
            object : ClassLoader(null) {
                override fun getResourceAsStream(name: String) =
                    if (name == "configs/sample.toml") {
                        ByteArrayInputStream(
                            content.toByteArray(StandardCharsets.UTF_8),
                        )
                    } else {
                        null
                    }
            }

        val resource =
            ConfigResource.classpath(
                "/configs/sample.toml",
                classLoader,
            )

        val loaded =
            resource.open().use { input ->
                input.readBytes().toString(StandardCharsets.UTF_8)
            }

        assertEquals(content, loaded)
    }
}
