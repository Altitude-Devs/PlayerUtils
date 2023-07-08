package com.alttd.playerutils.config;

import com.alttd.playerutils.util.Logger;

import java.io.File;
import java.util.List;

public class Messages extends AbstractConfig {
    static Messages config;
    private final Logger logger;

    Messages(Logger logger) {
        super(
                new File(System.getProperty("user.home") + File.separator
                        + "share" + File.separator
                        + "configs" + File.separator
                        + "PlayerUtils"),
                "messages.yml", logger);
        this.logger = logger;
    }

    public static void reload(Logger logger) {
        config = new Messages(logger);
        config.readConfig(Messages.class, null);
    }

    public static class HELP {
        private static final String prefix = "help.";

        public static String HELP_MESSAGE_WRAPPER = "<gold>PlayerUtils help:\n<commands></gold>";
        public static String HELP_MESSAGE = "<green>Show this menu: <gold>/pu help</gold></green>";
        public static String GLOW = "<green>Glow in a specified color: <gold>/pu glow <color></gold></green>";
        public static String XP_CHEQUE = "<green>Create an xp cheque: <gold>/pu xpcheque <amount></gold></green>";
        public static String XP_CALC = "<green>Calculate the amount of xp between levels: <gold>/pu xpcalc <from> <to></gold></green>";
        public static String RELOAD = "<green>Reload the configs for PlayerUtils: <gold>/pu reload</gold></green>";
        public static String ROTATE_BLOCK = "<green>Enable rotating blocks with a blaze rod: <gold>/pu rotateblock</gold></green>";

        @SuppressWarnings("unused")
        private static void load() {
            HELP_MESSAGE_WRAPPER = config.getString(prefix, "help-wrapper", HELP_MESSAGE_WRAPPER);
            HELP_MESSAGE = config.getString(prefix, "help", HELP_MESSAGE);
            GLOW = config.getString(prefix, "glow", GLOW);
            XP_CHEQUE = config.getString(prefix, "xp-cheque", XP_CHEQUE);
            XP_CALC = config.getString(prefix, "xp-calc", XP_CALC);
            RELOAD = config.getString(prefix, "reload", RELOAD);
            ROTATE_BLOCK = config.getString(prefix, "rotate-block", ROTATE_BLOCK);
        }
    }

    public static class GENERIC {
        private static final String prefix = "generic.";

        public static String NO_PERMISSION = "<red>You don't have permission for this command</red>";
        public static String PLAYER_ONLY = "<red>This command can only be executed as a player</red>";

        @SuppressWarnings("unused")
        private static void load() {
            NO_PERMISSION = config.getString(prefix, "no-permission", NO_PERMISSION);
            PLAYER_ONLY = config.getString(prefix, "player-only", PLAYER_ONLY);
        }
    }

    public static class GLOW {
        private static final String prefix = "pu-command.glow";

        public static String GLOW_OFF = "<green>Glow turned off</green>";
        public static String GLOW_ON = "<green>Glow turned on, you are now glowing <color></green>";
        public static String UNABLE_TO_GET_SCOREBOARD = "<red>Unable to get scoreboard for team</red>";

        @SuppressWarnings("unused")
        private static void load() {
            GLOW_OFF = config.getString(prefix, "glow-off",GLOW_OFF);
            GLOW_ON = config.getString(prefix, "glow-on",GLOW_ON);
            UNABLE_TO_GET_SCOREBOARD = config.getString(prefix, "unable-to-get-scoreboard",UNABLE_TO_GET_SCOREBOARD);
        }
    }

    public static class XP_CHEQUE {
        private static final String prefix = "pu-command.xp-cheque.";

        public static String FAILED_STORAGE = "<red>Unable to create custom item for xp cheque</red>";
        public static String NEGATIVE = "<red>You cannot enter a negative value for xp.</red>";
        public static String NOT_ENOUGH_XP = "<red>Not enough xp, you have <xp></red>";
        public static String NOT_HOLDING_BOTTLE = "<red>You need to hold an empty glass bottle while executing this command</red>";
        public static String DISPLAY_NAME = "<yellow>Xp bottle containing <xp> xp</yellow>";
        public static List<String> LORE = List.of("Issued by <name>", "Throw to retrieve xp");

        @SuppressWarnings("unused")
        private static void load() {
            FAILED_STORAGE = config.getString(prefix, "failed-storage", FAILED_STORAGE);
            NEGATIVE = config.getString(prefix, "negative", NEGATIVE);
            NOT_ENOUGH_XP = config.getString(prefix, "not-enough-xp", NOT_ENOUGH_XP);
            NOT_HOLDING_BOTTLE = config.getString(prefix, "not-holding-bottle", NOT_HOLDING_BOTTLE);
            DISPLAY_NAME = config.getString(prefix, "display-name", DISPLAY_NAME);
            LORE = config.getStringList(prefix, "lore", LORE);
        }
    }

    public static class XP_CALC {
        private static final String prefix = "pu-command.xp-cheque.";

        public static String XP_NEEDED = "<green>Xp needed <gold><xp_needed></gold>.</green>";

        @SuppressWarnings("unused")
        private static void load() {
            XP_NEEDED = config.getString(prefix, "xp-needed", XP_NEEDED);
        }
    }

    public static class RELOAD {
        private static final String prefix = "pu-command.reload.";

        public static String RELOADED = "<green>Reloaded configs</green>";

        @SuppressWarnings("unused")
        private static void load() {
            RELOADED = config.getString(prefix, "reloaded", RELOADED);
        }
    }

    public static class ROTATE_BLOCK {
        private static final String prefix = "pu-command.rotate-block.";

        public static String ENABLED = "<green>You enabled rotating blocks by left clicking them with a <gold>wooden hoe</gold></green>";
        public static String DISABLED = "<green>You <red>disabled</red> rotating blocks</green>";

        @SuppressWarnings("unused")
        private static void load() {
            ENABLED = config.getString(prefix, "enabled", ENABLED);
            DISABLED = config.getString(prefix, "disabled", DISABLED);
        }
    }
}
