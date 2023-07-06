package com.alttd.playerutils.util;

import com.alttd.playerutils.config.Config;

public class Logger {

    private final java.util.logging.Logger logger;
    static private final String RESET = "\u001B[0m";
    static private final String GREEN = "\u001B[32m";
    static private final String TEAL = "\u001B[36m";

    public Logger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    public void debug(String debug, String... variables) {
        if (!Config.SETTINGS.DEBUG)
            return;
        logger.info(TEAL + replace(debug, variables) + RESET);
    }

    public void info(String info, String... variables) {
        logger.info(GREEN + replace(info, variables) + RESET);
    }

    public void warning(String warning, String... variables) {
        if (!Config.SETTINGS.WARNINGS)
            return;
        logger.warning(replace(warning, variables));
    }

    public void severe(String severe, String... variables) {
        logger.severe(replace(severe, variables));
    }

    private String replace(String text, String... variables) {
        for (String variable : variables) {
            text = text.replaceFirst("%", variable);
        }
        return text;
    }
}
