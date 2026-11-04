package io.github.deopping.bamboo.configuration.internal

import io.github.deopping.bamboo.configuration.api.ConfigLoadOptions
import io.github.deopping.bamboo.configuration.api.ConfigResource
import io.github.deopping.bamboo.configuration.api.schema.ConfigSchema
import java.util.Optional

internal class DefaultConfigLoadOptions(
    override val resource: Optional<ConfigResource> = Optional.empty(),
    override val schema: Optional<ConfigSchema> = Optional.empty(),
) : ConfigLoadOptions
