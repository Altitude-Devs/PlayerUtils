package com.alttd.playerutils.event_listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportEvent implements Listener {

    /**
     * Modifies teleport behavior for mounted players by dismounting them
     *  and changing their teleport location to location + 1y
     *
     * @param event the PlayerTeleportEvent being triggered
     */
    @EventHandler()
    public void preventTeleportForMountedPlayers(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (player.getVehicle() == null)
            return;
        event.setCancelled(true);
        player.sendRichMessage("<red>Teleporting was cancelled. You can not be sitting, or mounted while teleporting.");
    }
}
