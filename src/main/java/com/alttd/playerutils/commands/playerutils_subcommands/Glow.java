package com.alttd.playerutils.commands.playerutils_subcommands;

import com.alttd.playerutils.commands.SubCommand;
import com.alttd.playerutils.config.Messages;
import com.alttd.playerutils.util.Logger;
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

    private final Logger logger;

    public Glow(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMiniMessage(Messages.GENERIC.PLAYER_ONLY, null);
            return true;
        }

        if (args.length != 2) {
            return false;
        }

        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        board.getTeams().stream()
                .filter(team -> team.getName().startsWith("Glow"))
                .filter(team -> team.hasPlayer(player))
                .forEach(team -> team.removePlayer(player));

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

        Team team = board.getTeam("Glow-" + dyeColor.name());

        if (team == null) {
            team = board.registerNewTeam("Glow-" + dyeColor.name());
            NamedTextColor namedTextColor = NamedTextColor.nearestTo(TextColor.color(dyeColor.getColor().asRGB()));
            team.color(namedTextColor);
        }

        if (team.getScoreboard() == null) {
            commandSender.sendMiniMessage(Messages.GLOW.UNABLE_TO_GET_SCOREBOARD, null);
            logger.warning("Unable to get scoreboard for team");
            return true;
        }

//        if (!team.hasEntry(player.getName())) {
//            team.addEntry(player.getName());
//        }
        if (!team.hasPlayer(player))
            team.addPlayer(player);
        player.setScoreboard(team.getScoreboard());

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
