package com.alttd.playerutils.commands.playerutils_subcommands;

import com.alttd.playerutils.commands.SubCommand;
import com.alttd.playerutils.config.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class XPCalc extends SubCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, String[] args) {
        if (args.length != 3) {
            return false;
        }
        Optional<Integer> optionalFrom = getInt(args[1]);
        Optional<Integer> optionalTo = getInt(args[2]);

        if (optionalFrom.isEmpty() || optionalTo.isEmpty())
            return false;

        long totalXpNeeded = 0;
        int startingLevel = optionalFrom.get();
        int endingLevel = optionalTo.get();
        if (endingLevel < startingLevel) {
            int tmp = startingLevel;
            startingLevel = endingLevel;
            endingLevel = tmp;
        }

        while (startingLevel < endingLevel) {
            totalXpNeeded += getExpToNext(startingLevel);
            startingLevel++;
        }

        commandSender.sendMiniMessage(Messages.XP_CALC.XP_NEEDED, Placeholder.parsed("xp_needed", String.valueOf(totalXpNeeded)));
        return true;
    }

    @Override
    public String getName() {
        return "xpcalc";
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, String[] args) {
        return List.of();
    }

    @Override
    public String getHelpMessage() {
        return Messages.HELP.XP_CALC;
    }

    private Optional<Integer> getInt(String arg) {
        try {
            return Optional.of(Integer.parseInt(arg));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public int getExpToNext(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        } else if (level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }
}
