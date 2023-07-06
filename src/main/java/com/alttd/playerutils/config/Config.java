package com.alttd.playerutils.config;

import com.alttd.playerutils.PlayerUtils;
import com.alttd.playerutils.util.Logger;

public class Config extends AbstractConfig{

    static Config config;
    private Logger logger;

    Config(PlayerUtils playerUtils, Logger logger) {
        super(playerUtils, "config.yml", logger);
        this.logger = logger;
    }

    public static void reload(PlayerUtils playerUtils, Logger logger) {
        logger.info("Reloading config");
        config = new Config(playerUtils, logger);
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
