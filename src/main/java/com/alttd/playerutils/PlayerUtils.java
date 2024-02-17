package com.alttd.playerutils;

import com.alttd.playerutils.commands.PlayerUtilsCommand;
import com.alttd.playerutils.commands.playerutils_subcommands.RotateBlock;
import com.alttd.playerutils.config.Config;
import com.alttd.playerutils.config.Messages;
import com.alttd.playerutils.event_listeners.RotateBlockEvent;
import com.alttd.playerutils.event_listeners.TeleportEvent;
import com.alttd.playerutils.event_listeners.XpBottleEvent;
import com.alttd.playerutils.util.Logger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerUtils extends JavaPlugin {

    private Logger logger;
    private PlayerUtilsCommand playerUtilsCommand;

    @Override
    public void onEnable() {
        this.logger = new Logger(getLogger());
        registerCommands();
        registerEvents();
        reloadConfigs();
    }

    @Override
    public void onDisable() {
    }

    private void registerCommands() {
        playerUtilsCommand = new PlayerUtilsCommand(this, logger);
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new XpBottleEvent(this, logger), this);
        pluginManager.registerEvents(new TeleportEvent(), this);

        RotateBlockEvent rotateBlockEvent = new RotateBlockEvent();
        pluginManager.registerEvents(rotateBlockEvent, this);
        playerUtilsCommand.addSubCommand(new RotateBlock(rotateBlockEvent));
    }

    public void reloadConfigs() {
        Config.reload(logger);
        Messages.reload(logger);
    }
}
