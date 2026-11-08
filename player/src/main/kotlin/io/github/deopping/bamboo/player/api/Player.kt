package io.github.deopping.bamboo.player.api

import java.util.UUID

/**
 * Represents a player connected to the server.
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface Player {
    /**
     * Gets the player's unique identifier.
     *
     * @return the player's UUID
     * @since 1.0.0
     */
    public val uniqueId: UUID

    /**
     * Gets the player's username.
     *
     * @return the player's username.
     * @since 1.0.0
     */
    public val username: String

    /**
     * Checks whether the player is currently connected.
     *
     * @return `true` if the player is connected
     * @since 1.0.0
     */
    public val isOnline: Boolean
}
