package me.j3ltr.rankedtkrhelper.commands;

import me.j3ltr.rankedtkrhelper.Config;
import me.j3ltr.rankedtkrhelper.RankedTkrHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;

public class RankedTkrHelperCommand extends CommandBase {
    private final RankedTkrHelper mod;

    public RankedTkrHelperCommand(RankedTkrHelper mod) {
        this.mod = mod;
    }

    @Override
    public String getCommandName() {
        return "rankedtkrhelper";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/rankedtkrhelper";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("rtkrh");
    }


    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        mod.setDisplayScreen(Config.INSTANCE.gui());
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
