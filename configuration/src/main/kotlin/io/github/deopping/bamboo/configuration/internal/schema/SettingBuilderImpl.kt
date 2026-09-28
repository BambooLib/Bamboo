package io.github.deopping.bamboo.configuration.internal.schema

import io.github.deopping.bamboo.configuration.api.schema.ConfigValidator
import io.github.deopping.bamboo.configuration.api.schema.SettingBuilder

internal class SettingBuilderImpl<T>(
    override val path: String,
    override val defaultValue: T
) : SettingBuilder<T> {

    override var comment: String? = null

    private var validator: ConfigValidator<T>? = null

    override fun validate(validator: ConfigValidator<T>): SettingBuilder<T> {
        this.validator = validator
        return this
    }

    fun build(): ConfigSettingImpl<T> {
        return ConfigSettingImpl(
            path = path,
            defaultValue = defaultValue,
            comment = comment,
            validator = validator
        )
    }

}