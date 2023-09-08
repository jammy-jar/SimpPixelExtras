package me.jammy.simppixelextras.command;

import com.google.common.collect.Lists;
import me.jammy.simppixelextras.SimpPixelExtras;
import me.jammy.simppixelextras.command.subcommand.ReloadSubCmd;
import me.jammy.simppixelextras.config.Lang;
import me.jammy.simppixelextras.config.Msgs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;

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
                Msgs.of(Lang.INSUFFICIENT_PERMISSIONS.getLang()).send(sender);
                return true;
            }

            if (!subCommand.run(sender, Arrays.copyOfRange(args, 1, args.length))) {
                List<String> msg = Lists.newArrayList(Lang.INVALID_FORMAT_FIRST.getLang());
                subCommand.getSyntax().forEach(syn ->
                        msg.add(Lang.INVALID_FORMAT_SYNTAX.getLang().replace("<syntax>", syn)));
                String formatError = String.join("<newline>", msg);

                Msgs.of(formatError).var("command", s).send(sender);
                return true;
            }

            return true;
        }

        Msgs.of(Lang.UNKNOWN_COMMAND.getLang()).send(sender);
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
