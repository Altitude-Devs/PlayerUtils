package com.alttd.playerutils.commands.playerutils_subcommands;

import com.alttd.playerutils.commands.SubCommand;
import com.alttd.playerutils.config.Messages;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Glow extends SubCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMiniMessage(Messages.GENERIC.PLAYER_ONLY, null);
            return true;
        }

        if (args.length != 2) {
            return false;
        }

        if (args[1].equalsIgnoreCase("off")) {
            commandSender.sendMiniMessage(Messages.GLOW.GLOW_OFF, null);
            player.setGlowing(false);
            return true;
        }

        Optional<DyeColor> any = Arrays.stream(DyeColor.values()).filter(chatColor -> chatColor.name().equalsIgnoreCase(args[1])).findAny();
        if (any.isEmpty()) {
            commandSender.sendMiniMessage(getHelpMessage(), null);
            return true;
        }

        DyeColor dyeColor = any.get();
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        Team team = board.getTeam("Glow " + dyeColor.name());

        if (team == null) {
            team = board.registerNewTeam("Glow " + dyeColor.name());
        }

        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }

        NamedTextColor namedTextColor = NamedTextColor.nearestTo(TextColor.color(dyeColor.getColor().asRGB()));
        team.color(namedTextColor);

        player.setGlowing(true);
        commandSender.sendMiniMessage(Messages.GLOW.GLOW_ON, Placeholder.parsed("color", dyeColor.name()));
        return true;
    }

    @Override
    public String getName() {
        return "glow";
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, String[] args) {
        if (args.length == 2) {
            return Arrays.stream(DyeColor.values()).map(Enum::name).collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public String getHelpMessage() {
        return Messages.HELP.GLOW;
    }
}
