package com.alttd.playerutils;

import com.alttd.playerutils.commands.PlayerUtilsCommand;
import com.alttd.playerutils.commands.playerutils_subcommands.RotateBlock;
import com.alttd.playerutils.config.Config;
import com.alttd.playerutils.config.Messages;
import com.alttd.playerutils.event_listeners.RotateBlockEvent;
import com.alttd.playerutils.event_listeners.XpBottleEvent;
import com.alttd.playerutils.util.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerUtils extends JavaPlugin {

    private Logger logger;
    private PlayerUtilsCommand playerUtilsCommand;

    @Override
    public void onEnable() {
        this.logger = new Logger(getLogger());
        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
    }

    private void registerCommands() {
        playerUtilsCommand = new PlayerUtilsCommand(this, logger);
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new XpBottleEvent(this, logger), this);

        RotateBlockEvent rotateBlockEvent = new RotateBlockEvent();
        getServer().getPluginManager().registerEvents(rotateBlockEvent, this);
        playerUtilsCommand.addSubCommand(new RotateBlock(rotateBlockEvent));
    }

    public void reloadConfigs() {
        Config.reload(logger);
        Messages.reload(logger);
    }
}
