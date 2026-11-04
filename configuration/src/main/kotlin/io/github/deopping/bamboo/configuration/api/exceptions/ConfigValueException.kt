package io.github.deopping.bamboo.configuration.api.exceptions

/**
 * Represents an error caused by a configuration value that does not match
 * the expected type.
 *
 * This exception is thrown when a configuration value cannot be interpreted
 * as the requested type.
 *
 * @property configPath the configuration path containing the invalid value
 * @property expectedType the expected type of the configuration value
 * @param actualValue the value that did not match the expected type
 *
 * @author DeOpping
 * @since 1.0.0
 */
public class ConfigValueException(
    public val configPath: String,
    public val expectedType: String,
    actualValue: Any,
) : ConfigException(
        "Invalid configuration value at '$configPath': " +
            "expected $expectedType, but found " +
            "${actualValue.javaClass.simpleName}.",
    )
