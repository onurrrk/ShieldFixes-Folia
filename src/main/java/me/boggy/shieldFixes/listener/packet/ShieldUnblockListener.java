package me.boggy.shieldFixes.listener.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.boggy.shieldFixes.ShieldFixes;

public class ShieldUnblockListener implements PacketListener {

    private final ShieldFixes plugin;

    public ShieldUnblockListener(ShieldFixes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketSend(PacketSendEvent e) {
        if (e.getPacketType() != PacketType.Play.Server.ENTITY_METADATA) {
            return;
        }

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(e);

        for (EntityData entityData : packet.getEntityMetadata()) {
            if (entityData.getIndex() == 8 && entityData.getValue() instanceof Byte && (Byte) entityData.getValue() == 0) {
                plugin.getBlockingPlayers().removeIf(id -> id == packet.getEntityId());
                return;
            }
        }
    }
}
