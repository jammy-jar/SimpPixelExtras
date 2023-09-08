package me.jammy.simppixelextras.command;

import com.google.common.collect.Lists;
import me.jammy.simppixelextras.SimpPixelExtras;
import me.jammy.simppixelextras.config.Msgs;
import me.jammy.simppixelextras.permission.Permission;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SignatureCmd implements TabExecutor {

    private final SimpPixelExtras plugin;

    public SignatureCmd(SimpPixelExtras plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!sender.hasPermission(Permission.SIGNATURE.asPerm())) {
            Msgs.of("<red>You have insufficient permissions to do this!").send(sender);
            return true;
        }

        String cmdNotRecognisedErr = """
                <red>Command not Recognised!\s
                <yellow>Usage:\s
                <red>/<label> <player (default: <yellow>You<red>)> (optional: -o)
                """.replace("<label>", label);
        boolean offhand = false;

        ArrayList<String> params = Lists.newArrayList(args);

        Player target = null;
        if (args.length > 0)
            target = Bukkit.getPlayerExact(params.remove(0));

        if (params.remove("-o"))
            offhand = true;

        // If the command is sent by the console, but the details are wrong, an error is sent.
        if (!(sender instanceof Player) && (target == null || params.size() == 0)) {
            Msgs.of(cmdNotRecognisedErr).send(sender);
            return true;
        }
        if (target == null)
            target = (Player) sender;

        final Component signature;
        if (sender.hasPermission(Permission.ADMIN_SIGNATURE.asPerm()) && params.size() > 0)
            signature = Msgs.of(String.join(" ", params)).asComp();
        else {
            if (!(sender instanceof Player)) {
                Msgs.of("<red>You are not a player! To apply a signature enter a custom signature after the player name.").send(sender);
                return true;
            }

            String cfgValue = plugin.getSignatureCfg().get().getString(((Player) sender).getUniqueId().toString());
            if (cfgValue == null) {
                Msgs.of("<red>You do not have a signature! Speak with management to amend this.").send(sender);
                return true;
            }
            else
                signature = Msgs.of(cfgValue).asComp();
        }

        ItemStack item = offhand ? target.getInventory().getItemInOffHand() : target.getInventory().getItemInMainHand();
        item.editMeta(m -> {
            if (!m.hasLore()) {
                m.lore(List.of(signature));
                return;
            }
            ArrayList<Component> lore = Lists.newArrayList(m.lore());
            lore.add(signature);
            m.lore(lore);
        });

        if (item.getType() == Material.AIR) {
            Msgs.of("<red>You cannot sign air!").send(sender);
            return true;
        }


        Msgs.of("<yellow>Signature: <signature> <yellow>was successfully applied to <light_purple><player><yellow>'s <item>")
                .cvar("signature", signature)
                .cvar("player", target.displayName())
                .cvar("item", item.displayName())
                .send(sender);

        Msgs announcement = Msgs.of("<aqua><sender> <yellow>signed <light_purple><player><yellow>'s <item><yellow>! With signature: <dark_purple><signature>")
                .cvar("player", target.displayName())
                .cvar("item", item.displayName())
                .cvar("signature", signature);

        Msgs msgs = sender instanceof Player player ? announcement.cvar("sender", player.displayName()) : announcement.var("sender", sender.getName());
        msgs.send(Audience.audience(plugin.getServer().getOnlinePlayers()));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> options = new ArrayList<>();
        List<String> players = plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
        options.add("-o");

        if (args.length == 1)
            options.addAll(StringUtil.copyPartialMatches(args[0], players, new ArrayList<>()));

        return options;
    }
}
