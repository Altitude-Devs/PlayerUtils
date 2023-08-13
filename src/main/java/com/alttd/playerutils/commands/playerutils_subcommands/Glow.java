package com.alttd.playerutils.commands.playerutils_subcommands;

import com.alttd.playerutils.commands.SubCommand;
import com.alttd.playerutils.config.Messages;
import com.alttd.playerutils.util.Logger;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
        if (args.length != 2 && args.length != 3) {
            return false;
        }

        boolean otherPlayer = args.length == 3;
        Optional<Player> playerFromArg = getTargetPlayer(commandSender, args);
        if (playerFromArg.isEmpty()) {
            return true;
        }

        Player player = playerFromArg.get();

        if (args[1].equalsIgnoreCase("off")) {
            turnOffGlow(commandSender, player, otherPlayer);
            return true;
        }

        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        board.getTeams().stream()
                .filter(team -> team.getName().startsWith("Glow"))
                .filter(team -> team.hasPlayer(player))
                .forEach(team -> team.removePlayer(player));

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

        turnOnGlow(commandSender, player, team, dyeColor, otherPlayer);
        return true;
    }

    private Optional<Player> getTargetPlayer(CommandSender commandSender, String[] args) {
        if (args.length == 2) {
            if (!(commandSender instanceof Player commandPlayer)) {
                commandSender.sendMiniMessage(Messages.GENERIC.PLAYER_ONLY, null);
                return Optional.empty();
            }
            return Optional.of(commandPlayer);
        }

        if (!commandSender.hasPermission(getPermission() + ".other")) {
            commandSender.sendMiniMessage(Messages.GENERIC.NO_PERMISSION, Placeholder.parsed("permission", getPermission() + ".other"));
            return Optional.empty();
        }
        Optional<? extends Player> any = Bukkit.getOnlinePlayers().stream().filter(offlinePlayer -> offlinePlayer.getName().equalsIgnoreCase(args[2])).findAny();
        if (any.isPresent()) {
            return Optional.of(any.get());
        } else {
            commandSender.sendMiniMessage(Messages.GENERIC.PLAYER_NOT_FOUND, Placeholder.parsed("player", args[2]));
            return Optional.empty();
        }
    }

    private void turnOffGlow(CommandSender commandSender, Player player, boolean otherPlayer) {
        player.sendMiniMessage(Messages.GLOW.GLOW_OFF, null);
        player.setGlowing(false);
        if (otherPlayer) {
            commandSender.sendMiniMessage(Messages.GLOW.GLOW_OFF_FOR_PLAYER, Placeholder.component("player", player.name()));
        }
    }

    private void turnOnGlow(CommandSender commandSender, Player player, Team team, DyeColor dyeColor, boolean otherPlayer) {
        if (team.getScoreboard() == null) {
            commandSender.sendMiniMessage(Messages.GLOW.UNABLE_TO_GET_SCOREBOARD, null);
            logger.warning("Unable to get scoreboard for team");
            return;
        }

        if (!team.hasPlayer(player))
            team.addPlayer(player);
        player.setScoreboard(team.getScoreboard());

        player.setGlowing(true);
        player.sendMiniMessage(Messages.GLOW.GLOW_ON, Placeholder.parsed("color", dyeColor.name()));
        if (otherPlayer) {
            commandSender.sendMiniMessage(Messages.GLOW.GLOW_ON_FOR_PLAYER, TagResolver.resolver(
                    Placeholder.parsed("color", dyeColor.name()),
                    Placeholder.component("player", player.name())
            ));
        }
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
        if (args.length == 3) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return List.of();
    }

    @Override
    public String getHelpMessage() {
        return Messages.HELP.GLOW;
    }
}
