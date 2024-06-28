package me.j3ltr.rankedtkrhelper.commands;

import me.j3ltr.rankedtkrhelper.RankedTkrHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import static me.j3ltr.rankedtkrhelper.utils.ClipboardUtil.copyToClipboard;

public class LastRaceCommand extends CommandBase {
    private final RankedTkrHelper mod;

    public LastRaceCommand(RankedTkrHelper mod) {
        this.mod = mod;
    }

    @Override
    public String getCommandName() {
        return "lastrace";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lastrace";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (mod.getPreviousRace() == null) {
            mod.sendMessage("No races have been recorded yet.");
            return;
        }

        copyToClipboard(mod.getPreviousRace().getDiscordCommand(mod.getCurrentRoundPlayers()));

        mod.sendMessage("The /race command of the last race has been copied to your clipboard.");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
