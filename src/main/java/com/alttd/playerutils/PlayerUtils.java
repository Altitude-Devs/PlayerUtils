package com.alttd.playerutils;

import com.alttd.playerutils.commands.PlayerUtilsCommand;
import com.alttd.playerutils.config.Config;
import com.alttd.playerutils.config.Messages;
import com.alttd.playerutils.event_listeners.XpBottleEvent;
import com.alttd.playerutils.util.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerUtils extends JavaPlugin {

    private Logger logger;

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
        new PlayerUtilsCommand(this, logger);
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new XpBottleEvent(this, logger), this);
    }

    public void reloadConfigs() {
        Config.reload(logger);
        Messages.reload(logger);
    }
}
