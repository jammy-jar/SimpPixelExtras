package me.jammy.simppixelextras.placeholder;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.jammy.simppixelextras.SimpPixelExtras;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SimpPixelExpansion extends PlaceholderExpansion {
    private final SimpPixelExtras plugin;

    public SimpPixelExpansion(SimpPixelExtras plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "jammy";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "simppixel";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] args = params.split("_");
        if (args.length < 1)
            return null;

        if (args[0].equalsIgnoreCase("current-land")) {
            if (!player.isOnline())
                return null;

            LandsIntegration api = plugin.getLandsApi();
            Player onlinePlayer = ((Player) player);
            Land land = api.getLandByChunk(onlinePlayer.getWorld(), onlinePlayer.getChunk().getX(), onlinePlayer.getChunk().getZ());
            if (land == null)
                return "Wilderness";

            return land.getName();
        }

        return null;
    }
}
