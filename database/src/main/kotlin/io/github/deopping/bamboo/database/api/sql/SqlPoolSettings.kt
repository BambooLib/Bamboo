package io.github.deopping.bamboo.database.api.sql

import io.github.deopping.bamboo.database.api.DatabaseConfiguration

/**
 * Configures connection-pool behavior for an SQL database.
 *
 * All timeout values are expressed in milliseconds. A `null`
 * value delegates to Bamboo's default for that setting.
 *
 * Pool settings are independent of JDBC driver properties. Use
 * [DatabaseConfiguration.Sql] to configure driver-specific options.
 *
 * @property maximumPoolSize maximum number of pooled connections
 * @property minimumIdle minimum number of idle connections to maintain
 * @property connectionTimeoutMillis maximum time to wait for a connection
 * @property validationTimeoutMillis maximum time allowed for a connection validation
 * @property initializationFailTimeoutMillis connection-pool startup behavior
 * @property idleTimeoutMillis maximum idle time before a connection may be removed
 * @property maxLifetimeMillis maximum lifetime of a pooled connection
 * @property keepaliveTimeMillis interval for checking idle connections
 * @property leakDetectionThresholdMillis threshold for reporting possible leaks;
 * zero disables leak detection
 *
 * @author DeOpping
 * @since 0.1.0
 */
data class SqlPoolSettings(

    /**
     * Maximum number of pooled connections
     * @since 0.1.0
     */
    val maximumPoolSize: Int? = null,

    /**
     * Minimum number of idle connections to maintain
     * @since 0.1.0
     */
    val minimumIdle: Int? = null,

    /**
     * Minimum Maximum time to wait for a connection
     * @since 0.1.0
     */
    val connectionTimeoutMillis: Long? = null,

    /**
     * Maximum time allowed for a connection validation
     * @since 0.1.0
     */
    val validationTimeoutMillis: Long? = null,

    /**
     * Connection-pool startup behavior
     * @since 0.1.0
     */
    val initializationFailTimeoutMillis: Long? = null,

    /**
     * Maximum idle time before a connection may be removed
     * @since 0.1.0
     */
    val idleTimeoutMillis: Long? = null,

    /**
     * Maximum lifetime of a pooled connection
     * @since 0.1.0
     */
    val maxLifetimeMillis: Long? = null,

    /**
     * Interval for checking idle connections
     * @since 0.1.0
     */
    val keepaliveTimeMillis: Long? = null,

    /**
     * Threshold for reporting possible leaks; zero disables leak detection
     * @since 0.1.0
     */
    val leakDetectionThresholdMillis: Long? = null
) {

    init {
        require(maximumPoolSize == null || maximumPoolSize > 0) {
            "Maximum pool size must be more than zero."
        }

        require(minimumIdle == null || minimumIdle >= 0) {
            "Minimum idle connections must not be negative."
        }

        require(maximumPoolSize == null ||
            minimumIdle == null ||
            minimumIdle <= maximumPoolSize
        ) {
            "Minimum idle connections cannot exceed maximum pool size."
        }

        require(connectionTimeoutMillis == null || connectionTimeoutMillis > 0) {
            "Connection timeout must be more than zero."
        }

        require(validationTimeoutMillis == null || validationTimeoutMillis > 0) {
            "Validation timeout must be more than zero."
        }

        require(idleTimeoutMillis == null || idleTimeoutMillis >= 0) {
            "Idle timeout must not be negative."
        }

        require(maxLifetimeMillis == null || maxLifetimeMillis >= 0) {
            "Maximum connection lifetime must not be negative."
        }

        require(keepaliveTimeMillis == null || keepaliveTimeMillis >= 0) {
            "Keepalive interval must not be negative."
        }

        require(leakDetectionThresholdMillis == null || leakDetectionThresholdMillis >= 0) {
            "Leak detection threshold must not be negative."
        }

        require(connectionTimeoutMillis == null ||
                validationTimeoutMillis == null ||
                validationTimeoutMillis < connectionTimeoutMillis
        ) {
            "Validation timeout must be less than connection timeout."
        }

        require(maxLifetimeMillis == null ||
            keepaliveTimeMillis == null ||
            maxLifetimeMillis == 0L ||
            keepaliveTimeMillis == 0L ||
            keepaliveTimeMillis < maxLifetimeMillis
        ) {
            "Keepalive interval must be less than maximum connection lifetime."
        }
    }

}