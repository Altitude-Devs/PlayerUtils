package com.alttd.playerutils.config;

import com.alttd.playerutils.util.Logger;

import java.io.File;

public class Config extends AbstractConfig{

    static Config config;
    private Logger logger;

    Config(Logger logger) {
        super(
                new File(System.getProperty("user.home") + File.separator
                        + "share" + File.separator
                        + "configs" + File.separator
                        + "PlayerUtils"),
                "config.yml", logger);
        this.logger = logger;
    }

    public static void reload(Logger logger) {
        logger.info("Reloading config");
        config = new Config(logger);
        config.readConfig(Config.class, null);
    }

    public static class SETTINGS {
        private static final String prefix = "settings.";
        public static boolean DEBUG = false;
        public static boolean WARNINGS = true;

        @SuppressWarnings("unused")
        private static void load() {
            DEBUG = config.getBoolean(prefix, "debug", DEBUG);
            WARNINGS = config.getBoolean(prefix, "warnings", WARNINGS);
        }
    }
}
