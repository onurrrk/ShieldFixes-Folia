package me.boggy.shieldFixes.listener.bukkit;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.boggy.shieldFixes.ShieldFixes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShieldListener implements Listener {

    private final ShieldFixes plugin;

    public ShieldListener(ShieldFixes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ItemStack is = e.getItem();

        if (is == null) {
            return;
        }

        if (is.getType() != Material.SHIELD || (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (e.getPlayer().hasCooldown(Material.SHIELD)) {
            return;
        }

        Player player = e.getPlayer();

        // Player is blocking

        plugin.getBlockingPlayers().add(player.getEntityId());

        List<Player> nearbyPlayers = new ArrayList<>();

        for (Entity entity : player.getNearbyEntities(40, 40, 40)) {
            if (entity instanceof Player) {
                nearbyPlayers.add((Player) entity);
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.getInventory().getItemInMainHand().getType() != Material.SHIELD || player.hasCooldown(Material.SHIELD)) {
                return;
            }

            EntityData entityData = new EntityData(8, EntityDataTypes.BYTE, (byte) 0x01);
            WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(player.getEntityId(), List.of(entityData));

            if (plugin.getBlockingPlayers().contains(player.getEntityId())) {
                nearbyPlayers.forEach(p -> PacketEvents.getAPI().getPlayerManager().sendPacket(p, packet));
            }

            plugin.getBlockingPlayers().removeIf(id -> id == player.getEntityId());

        }, 2L); // 2 ticks is the fastest you can send the packet

    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player damager)) {
            return;
        }

        if (!(e.getEntity() instanceof Player victim)) {
            return;
        }

        if (!victim.isBlocking() || !damager.getInventory().getItemInMainHand().getType().toString().endsWith("_AXE")) {
            return;
        }

        if (e.getFinalDamage() > 0) {
            return;
        }

        damager.playSound(victim.getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 1);
    }

}
