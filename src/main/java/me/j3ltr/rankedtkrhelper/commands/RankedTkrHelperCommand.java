package me.j3ltr.rankedtkrhelper.commands;

import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import gg.essential.api.commands.SubCommand;
import gg.essential.api.utils.GuiUtil;
import me.j3ltr.rankedtkrhelper.Config;
import me.j3ltr.rankedtkrhelper.RankedTkrHelper;
import me.j3ltr.rankedtkrhelper.utils.RaceUtil;

import java.util.HashSet;
import java.util.Set;

import static me.j3ltr.rankedtkrhelper.utils.ClipboardUtil.copyToClipboard;

public class RankedTkrHelperCommand extends Command {
    private final RankedTkrHelper mod;

    public RankedTkrHelperCommand(RankedTkrHelper mod) {
        super("rankedtkrhelper", false);

        this.mod = mod;
    }

    @Override
    public Set<Alias> getCommandAliases() {
        Set<Alias> aliases = new HashSet<>();
        aliases.add(new Alias("rtkrh"));
        return aliases;
    }

    @DefaultHandler
    public void handle() {
        GuiUtil.open(Config.INSTANCE.gui());
    }

    @SubCommand(value = "lastrace")
    public void handleLastRace() {
        if (mod.getPreviousRace() == null) {
            mod.sendMessage("No races have been recorded yet.");
            return;
        }

        copyToClipboard(RaceUtil.getDiscordCommand(mod.getPreviousRace(), mod.getCurrentRoundPlayers()));

        mod.sendMessage("The scoring command of the last race has been copied to your clipboard.");
    }
}
