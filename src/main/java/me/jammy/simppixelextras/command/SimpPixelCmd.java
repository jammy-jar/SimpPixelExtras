package me.jammy.simppixelextras.command;

import com.google.common.collect.Lists;
import me.dthbr.utils.config.Msgs;
import me.jammy.simppixelextras.SimpPixelExtras;
import me.jammy.simppixelextras.command.subcommand.ReloadSubCmd;
import me.jammy.simppixelextras.config.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpPixelCmd implements TabExecutor {

    private final List<SubCommand> subCommands = new ArrayList<>();

    public SimpPixelCmd(SimpPixelExtras plugin) {
        this.subCommands.add(new ReloadSubCmd(plugin));
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String[] args) {
        SubCommand subCmd = getSubCommands().stream()
                .filter(subCommand -> args[0].equalsIgnoreCase(subCommand.getName()))
                .findFirst().orElse(null);

        if (subCmd == null) {
            Msgs.of(Lang.UNKNOWN_COMMAND.getString()).send(sender);
        } else if (!subCmd.getPerm().hasPerm(sender)) {
            Msgs.of(Lang.INSUFFICIENT_PERMISSIONS.getString()).send(sender);
        } else if (!subCmd.run(sender, Arrays.copyOfRange(args, 1, args.length))) {
            Msgs.of(formatError(subCmd)).var("command", s).send(sender);
        }

        return true;

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String s, final @NotNull String[] args) {
        if (args.length == 1)
            return matchingSub(sender, args);
        return subCmdArgs(args);
    }

    private String formatError(SubCommand subCmd) {
        List<String> msg = Lists.newArrayList(Lang.INVALID_FORMAT_FIRST.getString());
        subCmd.getSyntax().forEach(syn ->
                msg.add(Lang.INVALID_FORMAT_SYNTAX.getString().replace("<syntax>", syn)));
        return String.join("<newline>", msg);
    }

    private List<String> matchingSub(@NotNull final CommandSender sender, final @NotNull String[] args) {
        Set<String> subCommandNames = getSubCommands().stream()
                .filter(subCommand -> subCommand.getPerm().hasPerm(sender))
                .map(SubCommand::getName)
                .collect(Collectors.toSet());

        return StringUtil.copyPartialMatches(args[0], subCommandNames, new ArrayList<>());
    }

    private List<String> subCmdArgs(final @NotNull String[] args) {
        String[] restOfArgs = Arrays.copyOfRange(args, 1, args.length);
        SubCommand subCmd = getSubCommands().stream()
                .filter(subCommand -> args[0].equalsIgnoreCase(subCommand.getName()))
                .filter(subCommand -> subCommand.getArguments(restOfArgs) != null)
                .findFirst().orElse(null);

        return subCmd == null ?
                new ArrayList<>() :
                StringUtil.copyPartialMatches(args[args.length - 1], subCmd.getArguments(restOfArgs), new ArrayList<>());
    }

    public List<SubCommand> getSubCommands() {
        return subCommands;
    }
}
