package io.github.deopping.bamboo.configuration.api.migration

/**
 * Performs a configuration migration.
 *
 * This interface is a Kotlin SAM interface and can therefore be implemented
 * with a lambda in both Kotlin and Java.
 *
 * @author DeOpping
 * @since 0.1.0
 */
fun interface ConfigMigrationAction {

    /**
     * Applies the migration to a configuration.
     *
     * @param scope migration scope used to modify the configuration
     * @since 0.1.0
     */
    fun apply(scope: ConfigMigrationScope)

}