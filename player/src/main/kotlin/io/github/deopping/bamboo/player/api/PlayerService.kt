package io.github.deopping.bamboo.player.api

import java.util.Optional
import java.util.UUID

/**
 * Provides access to players connected to the server
 *
 * @author DeOpping
 * @since 1.0.0
 */
public interface PlayerService {
    /**
     * Gets an online player by their unique identifier.
     *
     * @param uniqueId the player's UUID
     * @return the player if they are online, an empty optional otherwise
     * @since 1.0.0
     */
    public fun getPlayer(uniqueId: UUID): Optional<Player>

    /**
     * Gets a snapshot of all currently online players.
     *
     * @return a collection of online players
     * @since 1.0.0
     */
    public fun getOnlinePlayers(): Collection<Player>
}
