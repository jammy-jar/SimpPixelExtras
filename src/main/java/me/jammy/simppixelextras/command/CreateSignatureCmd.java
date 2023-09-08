package me.jammy.simppixelextras.command;

import com.google.common.collect.Lists;
import me.jammy.simppixelextras.SimpPixelExtras;
import me.jammy.simppixelextras.config.Msgs;
import me.jammy.simppixelextras.permission.Permission;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CreateSignatureCmd implements TabExecutor {

    private final SimpPixelExtras plugin;

    public CreateSignatureCmd(SimpPixelExtras plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!sender.hasPermission(Permission.ADMIN_SIGNATURE.asPerm())) {
            Msgs.of("<red>You have insufficient permissions to do this!").send(sender);
            return true;
        }

        String cmdNotRecognisedErr = """
                <red>Command not Recognised!\s
                <yellow>Usage:\s
                <red>/<label> <player> <signature>
                """.replace("<label>", label);

        ArrayList<String> params = Lists.newArrayList(args);
        if (args.length == 0) {
            Msgs.of(cmdNotRecognisedErr).send(sender);
            return true;
        }

        Player target = Bukkit.getPlayerExact(params.remove(0));

        // If the command is sent by the console, but the details are wrong, an error is sent.
        if (target == null || params.size() == 0) {
            Msgs.of(cmdNotRecognisedErr).send(sender);
            return true;
        }

        final String signature = String.join(" ", params);
        plugin.getSignatureCfg().set(target.getUniqueId().toString(), signature);

        Msgs.of("<yellow>Signature: <signature> <yellow>was successfully created for <light_purple><player><yellow>")
                .cvar("signature", Msgs.of(signature).asComp())
                .cvar("player", target.displayName())
                .send(Audience.audience(sender, target));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> options = new ArrayList<>();
        List<String> players = plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList();

        if (args.length == 1)
            options.addAll(StringUtil.copyPartialMatches(args[0], players, new ArrayList<>()));

        return options;
    }
}
