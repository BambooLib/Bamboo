package io.github.deopping.bamboo.configuration.internal.schema

import io.github.deopping.bamboo.configuration.api.schema.ConfigSetting
import io.github.deopping.bamboo.configuration.api.schema.ConfigValidator

internal class ConfigSettingImpl<T>(
    override val path: String,
    override val defaultValue: Any?,
    override val comment: String?,
    private val validator: ConfigValidator<T>?
) : ConfigSetting {

    override fun isValid(value: Any?): Boolean {
        if (validator == null) {
            return true
        }

        return try {
            @Suppress("UNCHECKED_CAST")
            validator.validate(value as T)
        } catch (_: ClassCastException) {
            false
        }
    }

}