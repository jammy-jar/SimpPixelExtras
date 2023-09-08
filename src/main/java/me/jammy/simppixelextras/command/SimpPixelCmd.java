package me.jammy.simppixelextras.command;

import me.jammy.simppixelextras.SimpPixelExtras;
import me.jammy.simppixelextras.command.subcommand.ReloadSubCmd;
import me.jammy.simppixelextras.config.Msgs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SimpPixelCmd implements TabExecutor {

    private final List<SubCommand> subCommands = new ArrayList<>();

    public SimpPixelCmd(SimpPixelExtras plugin) {
        this.subCommands.add(new ReloadSubCmd(plugin));
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String[] args) {
        for (SubCommand subCommand : getSubCommands()) {
            if (!args[0].equalsIgnoreCase(subCommand.getName()))
                continue;

            if (!sender.hasPermission(subCommand.getRequiredPermission())) {
                Msgs.of("<red>You don't have permission to do this!").send(sender);
                return true;
            }

            if (!subCommand.run(sender, Arrays.copyOfRange(args, 1, args.length))) {
                // TODO: Rework this bit as is messy.
                StringBuilder builder = new StringBuilder("<red>Invalid Command! \n<yellow>Format: ");

                List<String> syntaxList = subCommand.getSyntax();
                for (int i = 0; i < syntaxList.size(); i++) {
                    builder.append("\n<yellow> - /q ").append(syntaxList.get(i));
                    if (i < syntaxList.size() - 1) {
                        builder.append("\n<yellow> OR");
                    }
                }

                Msgs.of(builder.toString()).send(sender);
                return true;
            }

            return true;
        }

        Msgs.of("<red>This command does not exist!").send(sender);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            Set<String> subCommandNames = new HashSet<>();

            getSubCommands().forEach(subcommand -> {
                if (sender.hasPermission(subcommand.getRequiredPermission()))
                    subCommandNames.add(subcommand.getName());
            });

            options.addAll(StringUtil.copyPartialMatches(args[0], subCommandNames, new ArrayList<>()));
        } else {
            for (SubCommand subCommand : getSubCommands()) {
                if (args[0].equalsIgnoreCase(subCommand.getName())) {
                    if (subCommand.getArguments(Arrays.copyOfRange(args, 1, args.length)) == null)
                        options = null;
                    else {
                        //noinspection DataFlowIssue
                        options.addAll(StringUtil.copyPartialMatches(args[args.length - 1], subCommand.getArguments(Arrays.copyOfRange(args, 1, args.length)), new ArrayList<>()));
                    }
                }
            }
        }

        return options;
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }
}
