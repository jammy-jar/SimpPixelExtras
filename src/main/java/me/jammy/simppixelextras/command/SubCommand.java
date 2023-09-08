package me.jammy.simppixelextras.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SubCommand {

    public abstract String getName();

    public abstract String getDescription();

    public abstract List<String> getSyntax();

    public abstract String getRequiredPermission();

    public abstract List<String> getArguments(String[] args);

    public abstract boolean run(CommandSender sender, String[] args);
}
