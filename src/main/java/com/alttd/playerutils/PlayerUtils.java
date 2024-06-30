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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PlayerUtils extends JavaPlugin {

    private Logger logger;
    private PlayerUtilsCommand playerUtilsCommand;

    @Override
    public void onEnable() {
        this.logger = new Logger(getLogger());
        registerCommands();
        registerEvents();
        reloadConfigs();
        printVersion();
    }

    private void printVersion() {
        Properties gitProps = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("git.properties")) {
            gitProps.load(inputStream);
        } catch (IOException e) {
            logger.severe("Unable to load git.properties, unknown version");
            return;
        }

        logger.info("Git commit ID: %s".formatted(gitProps.getProperty("git.commit.id")));
        logger.info("Git commit time: %s".formatted(gitProps.getProperty("git.commit.time")));
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
