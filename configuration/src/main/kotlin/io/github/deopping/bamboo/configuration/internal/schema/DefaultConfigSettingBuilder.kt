package io.github.deopping.bamboo.configuration.internal.schema

import io.github.deopping.bamboo.configuration.api.schema.ConfigSetting
import io.github.deopping.bamboo.configuration.api.schema.ConfigSettingBuilder
import io.github.deopping.bamboo.configuration.api.schema.ConfigValidator
import java.util.Optional

internal class DefaultConfigSettingBuilder<T : Any>(
    private val path: String,
    private val defaultValue: T,
) : ConfigSettingBuilder<T> {
    init {
        require(path.isNotBlank()) {
            "Config setting path cannot be blank."
        }
    }

    private var comment: String? = null
    private var validator: ConfigValidator<T>? = null

    override fun comment(comment: String): ConfigSettingBuilder<T> {
        require(comment.isNotBlank()) {
            "Configuration comment cannot be blank."
        }

        this.comment = comment
        return this
    }

    override fun validator(validator: ConfigValidator<T>): ConfigSettingBuilder<T> {
        this.validator = validator
        return this
    }

    override fun build(): ConfigSetting<T> {
        require(validator?.validate(defaultValue) != false) {
            "Default value does not satisfy the configuration setting validator: $path"
        }

        return DefaultConfigSetting(
            path = path,
            defaultValue = defaultValue,
            comment = Optional.ofNullable(comment),
            validator = Optional.ofNullable(validator),
        )
    }
}
