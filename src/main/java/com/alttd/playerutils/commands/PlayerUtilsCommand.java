package com.alttd.playerutils.commands;

import com.alttd.playerutils.PlayerUtils;
import com.alttd.playerutils.commands.playerutils_subcommands.*;
import com.alttd.playerutils.config.Messages;
import com.alttd.playerutils.event_listeners.RotateBlockEvent;
import com.alttd.playerutils.util.Logger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerUtilsCommand implements CommandExecutor, TabExecutor {
    private final List<SubCommand> subCommands;

    public PlayerUtilsCommand(PlayerUtils playerUtils, Logger logger) {
        PluginCommand command = playerUtils.getCommand("playerutils");
        if (command == null) {
            subCommands = null;
            logger.severe("Unable to find transfer command.");
            return;
        }
        command.setExecutor(this);
        command.setTabCompleter(this);
        command.setAliases(List.of("pu"));

        subCommands = new ArrayList<>(List.of(
                new Glow(logger),
                new XPCheque(playerUtils),
                new XPCalc(),
                new Reload(playerUtils))
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String cmd, @NotNull String[] args) {
        if (args.length == 0) {
            commandSender.sendMiniMessage(Messages.HELP.HELP_MESSAGE_WRAPPER.replaceAll("<commands>", subCommands.stream()
                    .filter(subCommand -> commandSender.hasPermission(subCommand.getPermission()))
                    .map(SubCommand::getHelpMessage)
                    .collect(Collectors.joining("\n"))), null);
            return true;
        }

        SubCommand subCommand = getSubCommand(args[0]);
        if (subCommand == null)
            return false;

        if (!commandSender.hasPermission(subCommand.getPermission())) {
            commandSender.sendMiniMessage(Messages.GENERIC.NO_PERMISSION, Placeholder.parsed("permission", subCommand.getPermission()));
            return true;
        }

        boolean executedCorrectly = subCommand.onCommand(commandSender, args);
        if (!executedCorrectly) {
            commandSender.sendMiniMessage(subCommand.getHelpMessage(), null);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String cmd, @NotNull String[] args) {
        List<String> res = new ArrayList<>();

        if (args.length <= 1) {
            res.addAll(subCommands.stream()
                    .filter(subCommand -> commandSender.hasPermission(subCommand.getPermission()))
                    .map(SubCommand::getName)
                    .filter(name -> args.length == 0 || name.startsWith(args[0]))
                    .toList()
            );
        } else {
            SubCommand subCommand = getSubCommand(args[0]);
            if (subCommand != null && commandSender.hasPermission(subCommand.getPermission()))
                res.addAll(subCommand.getTabComplete(commandSender, args).stream()
                        .filter(str -> str.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                        .toList());
        }
        return res;
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }

    private SubCommand getSubCommand(String cmdName) {
        return subCommands.stream()
                .filter(subCommand -> subCommand.getName().equals(cmdName))
                .findFirst()
                .orElse(null);
    }

    public void addSubCommand(SubCommand subCommand) {
        if (subCommands.stream().anyMatch(entry -> entry.getName().equalsIgnoreCase(subCommand.getName()))) {
            return;
        }
        subCommands.add(subCommand);
    }
}