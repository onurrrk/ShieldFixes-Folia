package me.boggy.shieldFixes;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.boggy.shieldFixes.listener.bukkit.ShieldListener;
import me.boggy.shieldFixes.listener.packet.ShieldUnblockListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ShieldFixes extends JavaPlugin {

    private final Queue<Integer> blockingPlayers = new ConcurrentLinkedQueue<>();

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().getEventManager().registerListener(new ShieldUnblockListener(this), PacketListenerPriority.LOW);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new ShieldListener(this), this);
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    public Queue<Integer> getBlockingPlayers() {
        return blockingPlayers;
    }
}
