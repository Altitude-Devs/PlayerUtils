package com.alttd.playerutils.event_listeners;

import com.alttd.playerutils.PlayerUtils;
import com.alttd.playerutils.util.Logger;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class XpBottleEvent implements Listener {

    private final PlayerUtils playerUtils;
    private final Logger logger;

    public XpBottleEvent(PlayerUtils playerUtils, Logger logger) {
        this.playerUtils = playerUtils;
        this.logger = logger;
    }

    @EventHandler
    public void onExpBottle(ExpBottleEvent event) {
        ItemStack item = event.getEntity().getItem();
        PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey customXp = NamespacedKey.fromString("custom_xp", playerUtils);
        if (customXp == null) {
            logger.warning("Unable to retrieve name spaced key.");
            return;
        }
        Integer integer = persistentDataContainer.get(customXp, PersistentDataType.INTEGER);
        if (integer == null) {
            return;
        }
        event.setExperience(integer);
    }

}
