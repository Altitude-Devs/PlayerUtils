package com.alttd.playerutils.commands.playerutils_subcommands;

import com.alttd.playerutils.PlayerUtils;
import com.alttd.playerutils.commands.SubCommand;
import com.alttd.playerutils.config.Messages;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class XPCheque extends SubCommand {

    private final PlayerUtils playerUtils;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public XPCheque(PlayerUtils playerUtils) {
        this.playerUtils = playerUtils;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMiniMessage(Messages.GENERIC.PLAYER_ONLY, null);
            return true;
        }

        if (args.length != 2)
            return false;

        int xpValue;
        try {
            xpValue = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return false;
        }

        if ((xpValue <= 0)) {
            commandSender.sendMiniMessage(Messages.XP_CHEQUE.NEGATIVE, null);
            return true;
        }

        int totalExperience = player.getTotalExperience();
        if (totalExperience < xpValue) {
            commandSender.sendMiniMessage(Messages.XP_CHEQUE.NOT_ENOUGH_XP, Placeholder.parsed("xp", String.valueOf(totalExperience)));
            return true;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!heldItem.getType().equals(Material.GLASS_BOTTLE)) {
            commandSender.sendMiniMessage(Messages.XP_CHEQUE.NOT_HOLDING_BOTTLE, null);
            return true;
        }

        Optional<ItemStack> optionalItemStack = getExpBottleItem(player, xpValue);
        if (optionalItemStack.isEmpty())
            return true;

        ItemStack xpBottle = optionalItemStack.get();

        decreaseExperience(player, xpValue);

        if (heldItem.getAmount() == 1) {
            player.getInventory().setItemInMainHand(xpBottle);
            player.updateInventory();
            return true;
        }

        heldItem.setAmount(heldItem.getAmount() - 1);
        player.getInventory().addItem(xpBottle).values()
                .forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
        player.updateInventory();
        return true;
    }

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
        itemMeta.displayName(miniMessage.deserialize(Messages.XP_CHEQUE.DISPLAY_NAME, Placeholder.parsed("xp", String.valueOf(xpValue))));
        itemMeta.lore(Messages.XP_CHEQUE.LORE.stream()
                .map(str -> miniMessage.deserialize(str, Placeholder.component("name", player.displayName())))
                .collect(Collectors.toList()));
        expBottle.setItemMeta(itemMeta);
        return Optional.of(expBottle);
    }

    public void decreaseExperience(Player player, int xpToRemove) {
        int totalExp = player.getTotalExperience();
        int newTotalExp = Math.max(totalExp - xpToRemove, 0);

        int level = 0;
        while (true) {
            int expToNextLevel = getExpToNext(level);
            if (newTotalExp < expToNextLevel)
                break;
            newTotalExp -= expToNextLevel;
            level++;
        }

        float progress = (float) newTotalExp / getExpToNext(level);

        player.setTotalExperience(totalExp - xpToRemove);
        player.setLevel(level);
        player.setExp(progress);
    }

    public int getExpToNext(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        } else if (level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }

    @Override
    public String getName() {
        return "xpcheque";
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, String[] args) {
        return List.of();
    }

    @Override
    public String getHelpMessage() {
        return Messages.HELP.XP_CHEQUE;
    }
}
