package me.jammy.simppixelextras.command;

import com.google.common.collect.Lists;
import me.dthbr.utils.config.Msgs;
import me.jammy.simppixelextras.SimpPixelExtras;
import me.jammy.simppixelextras.config.Lang;
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

import java.util.ArrayList;
import java.util.List;

public class SignatureCmd implements TabExecutor {

    private final SimpPixelExtras plugin;

    private static final String SYNTAX = "<player> <signature>";

    public SignatureCmd(SimpPixelExtras plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!Permission.SIGNATURE.hasPerm(sender)) {
            Msgs.of(Lang.INSUFFICIENT_PERMISSIONS.getString()).send(sender);
            return true;
        }

        Msgs cmdNotRecognisedMsg = Msgs.of(Lang.INVALID_FORMAT_FIRST.getString() + "<newline>" + Lang.INVALID_FORMAT_SYNTAX)
                .var("command", label)
                .var("syntax", SYNTAX);

        boolean offhand = false;

        ArrayList<String> params = Lists.newArrayList(args);

        Player target = null;
        if (args.length > 0)
            target = Bukkit.getPlayerExact(params.remove(0));

        if (params.remove("-o"))
            offhand = true;

        // If the command is sent by the console, but the details are wrong, an error is sent.
        if (!(sender instanceof Player) && (target == null || params.size() == 0)) {
            cmdNotRecognisedMsg.send(sender);
            return true;
        }
        if (target == null)
            target = (Player) sender;

        final Component signature;
        if (Permission.ADMIN_SIGNATURE.hasPerm(sender) && params.size() > 0)
            signature = Msgs.of(String.join(" ", params)).asComp();
        else {
            if (!(sender instanceof Player)) {
                Msgs.of(Lang.SIG_CONSOLE_ERR.getString()).send(sender);
                return true;
            }

            String cfgValue = plugin.getSignatureCfg().get().getString(((Player) sender).getUniqueId().toString());
            if (cfgValue == null) {
                Msgs.of(Lang.NO_EXISTING_SIG.getString()).send(sender);
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
            Msgs.of(Lang.SIG_AIR_ERR.getString()).send(sender);
            return true;
        }


        Msgs.of(Lang.SIG_APPLIED_SUCCESS.getString())
                .cvar("signature", signature)
                .cvar("player", target.displayName())
                .cvar("item", item.displayName())
                .send(sender);

        Msgs announcement = Msgs.of(Lang.SIG_APPLIED_BROADCAST.getString())
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
