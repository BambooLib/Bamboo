package io.github.deopping.bamboo.configuration.internal.schema

import io.github.deopping.bamboo.configuration.api.schema.ConfigSetting
import io.github.deopping.bamboo.configuration.api.schema.ConfigValidator
import java.util.Optional

internal class DefaultConfigSetting<T : Any>(
    override val path: String,
    override val defaultValue: T,
    override val comment: Optional<String>,
    private val validator: Optional<ConfigValidator<T>>,
) : ConfigSetting<T> {
    init {
        require(path.isNotBlank()) {
            "Config setting path cannot be blank."
        }

        if (validator.isPresent) {
            require(validator.get().validate(defaultValue)) {
                "Default value does not satisfy the configuration setting validator: $path"
            }
        }
    }

    override fun isValid(value: Any?): Boolean {
        if (validator.isEmpty) {
            return true
        }

        return try {
            @Suppress("UNCHECKED_CAST")
            validator.get().validate(value as T)
        } catch (_: ClassCastException) {
            false
        }
    }
}
