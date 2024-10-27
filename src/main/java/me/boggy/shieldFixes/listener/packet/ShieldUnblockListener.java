package me.boggy.shieldFixes.listener.packet;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.boggy.shieldFixes.ShieldFixes;

public class ShieldUnblockListener implements com.github.retrooper.packetevents.event.PacketListener {

    private final ShieldFixes plugin;

    public ShieldUnblockListener(ShieldFixes plugin) {
        this.plugin = plugin;
    }

    /*
       Detects when a player unblocks their shield so that if
       the player blocks and unblocks within the 2 tick window
       their shield doesn't stay visually blocking
    */
    @Override
    public void onPacketSend(PacketSendEvent e) {

        if (e.getPacketType() != PacketType.Play.Server.ENTITY_METADATA) {
            return;
        }

        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(e);

        for (EntityData entityData : packet.getEntityMetadata()) {
            if (entityData.getIndex() == 8 && entityData.getValue() instanceof Byte && (Byte) entityData.getValue() == 0) {
                synchronized (plugin.getBlockingPlayers()) {
                    plugin.getBlockingPlayers().removeIf(id -> id == packet.getEntityId());
                }
                return;
            }
        }
    }

}
