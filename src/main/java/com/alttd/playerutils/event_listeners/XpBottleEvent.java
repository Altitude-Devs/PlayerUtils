package com.alttd.playerutils.event_listeners;

import com.alttd.playerutils.PlayerUtils;
import com.alttd.playerutils.config.Messages;
import com.alttd.playerutils.util.Logger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class XpBottleEvent implements Listener {

    private final PlayerUtils playerUtils;
    private final Logger logger;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

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

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof FurnaceInventory furnaceInventory)) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Furnace furnace = furnaceInventory.getHolder();
        if (furnace == null) {
            return;
        }

        if (event.getRawSlot() != 0) {
            return;
        }

        ItemStack itemStack = event.getCursor();
        if (itemStack.getType() != Material.GLASS_BOTTLE) {
            return;
        }

        int exp = 0;
        Map<CookingRecipe<?>, Integer> recipesUsed = furnace.getRecipesUsed();
        if (recipesUsed.isEmpty()) {
            return;
        }

        for (Map.Entry<CookingRecipe<?>, Integer> entry : recipesUsed.entrySet()) {
            exp += entry.getKey().getExperience() * entry.getValue();
        }

        Optional<ItemStack> optionalItemStack = getExpBottleItem(player, exp);
        if (optionalItemStack.isEmpty())
            return;
        furnace.clearRecipeUsed();

        event.setCurrentItem(optionalItemStack.get());
        itemStack.subtract(1);
    }

    // These should be moved to a util class so the command and the expbottle events can use them
    private Optional<ItemStack> getExpBottleItem(Player player, int xpValue) {
        ItemStack expBottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta itemMeta = expBottle.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        NamespacedKey customXp = NamespacedKey.fromString("custom_xp", playerUtils);

        if (customXp == null) {
            player.sendMiniMessage(Messages.XP_CHEQUE.FAILED_STORAGE, null);
            return Optional.empty();
        }
        persistentDataContainer.set(customXp, PersistentDataType.INTEGER, xpValue);
        String xpWithDots = addDots(String.valueOf(xpValue));
        itemMeta.displayName(miniMessage.deserialize(Messages.XP_CHEQUE.DISPLAY_NAME, Placeholder.parsed("xp", xpWithDots)));
        itemMeta.lore(Messages.XP_CHEQUE.LORE.stream()
                .map(str -> miniMessage.deserialize(str, TagResolver.resolver(
                        Placeholder.component("name", player.displayName()),
                        Placeholder.parsed("xp", xpWithDots)
                )))
                .collect(Collectors.toList()));
        expBottle.setItemMeta(itemMeta);
        return Optional.of(expBottle);
    }

    public String addDots(String input) {
        StringBuilder stringBuilder = new StringBuilder(input);
        int length = input.length();

        // Start from the third character from the right and add a dot after every third character
        for (int i = length - 3; i > 0; i -= 3) {
            stringBuilder.insert(i, '.');
        }

        return stringBuilder.toString();
    }

}
