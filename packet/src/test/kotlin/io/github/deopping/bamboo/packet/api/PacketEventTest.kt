package io.github.deopping.bamboo.packet.api

import io.github.deopping.bamboo.player.api.Player
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

private class PacketEventTest {
    private class TestPlayer : Player {
        public override val uniqueId: UUID = UUID.randomUUID()
        public override val username: String = "TestPlayer"
        public override val isOnline: Boolean = true
    }

    private class TestInboundPacket : InboundPacketView

    private class TestOutboundPacket : OutboundPacketView

    @Test
    fun `receive event exposes player and packet`() {
        val player = TestPlayer()
        val packet = TestInboundPacket()
        val event = PacketReceiveEvent(player, packet)

        assertSame(player, event.player)
        assertSame(packet, event.packet)
        assertFalse(event.isCanceled)

        event.isCanceled = true

        assertTrue(event.isCanceled)
    }

    @Test
    fun `send event exposes player and packet`() {
        val player = TestPlayer()
        val packet = TestOutboundPacket()
        val event = PacketSendEvent(player, packet)

        assertSame(player, event.player)
        assertSame(packet, event.packet)
        assertFalse(event.isCanceled)
    }
}
