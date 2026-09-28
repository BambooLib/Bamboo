package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.ConfigLoadOptions
import io.github.deopping.bamboo.configuration.api.ConfigResource
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchema

internal data class ConfigLoadOptionsImpl(
    override var resource: ConfigResource? = null,
    override var schema: ConfigSchema? = null
) : ConfigLoadOptions