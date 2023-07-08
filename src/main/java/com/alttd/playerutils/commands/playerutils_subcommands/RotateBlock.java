package com.alttd.playerutils.commands.playerutils_subcommands;

import com.alttd.playerutils.commands.SubCommand;
import com.alttd.playerutils.config.Messages;
import com.alttd.playerutils.event_listeners.RotateBlockEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RotateBlock extends SubCommand {

    private final RotateBlockEvent rotateBlockEvent;

    public RotateBlock(RotateBlockEvent rotateBlockEvent) {
        this.rotateBlockEvent = rotateBlockEvent;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMiniMessage(Messages.GENERIC.PLAYER_ONLY, null);
            return true;
        }

        boolean enabled = rotateBlockEvent.toggleRotate(player.getUniqueId());
        if (enabled)
            commandSender.sendMiniMessage(Messages.ROTATE_BLOCK.ENABLED, null);
        else
            commandSender.sendMiniMessage(Messages.ROTATE_BLOCK.DISABLED, null);
        return true;
    }

    @Override
    public String getName() {
        return "rotateblock";
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, String[] args) {
        return List.of();
    }

    @Override
    public String getHelpMessage() {
        return Messages.HELP.ROTATE_BLOCK;
    }
}
