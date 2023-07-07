package com.alttd.playerutils.commands.playerutils_subcommands;

import com.alttd.playerutils.PlayerUtils;
import com.alttd.playerutils.commands.SubCommand;
import com.alttd.playerutils.config.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reload extends SubCommand {

    private final PlayerUtils playerUtils;

    public Reload(PlayerUtils playerUtils) {
        this.playerUtils = playerUtils;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        playerUtils.reloadConfigs();
        commandSender.sendMiniMessage(Messages.RELOAD.RELOADED, null);
        return true;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, String[] args) {
        return List.of();
    }

    @Override
    public String getHelpMessage() {
        return Messages.HELP.RELOAD;
    }
}
