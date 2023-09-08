package me.jammy.simppixelextras.command.subcommand;

import me.jammy.simppixelextras.SimpPixelExtras;
import me.jammy.simppixelextras.command.SubCommand;
import me.jammy.simppixelextras.config.Lang;
import me.jammy.simppixelextras.config.Msgs;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadSubCmd extends SubCommand {
    private final SimpPixelExtras plugin;

    public ReloadSubCmd(SimpPixelExtras plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin";
    }

    @Override
    public List<String> getSyntax() {
        return null;
    }

    @Override
    public String getRequiredPermission() {
        return null;
    }

    @Override
    public List<String> getArguments(final String[] args) {
        return null;
    }

    @Override
    public boolean run(final CommandSender sender, final String[] args) {
        plugin.reloadConfig();
        plugin.getSignatureCfg().reload();
        Msgs.of(Lang.RELOAD_SUCCESSFUL.getLang()).send(sender);
        return true;
    }
}
