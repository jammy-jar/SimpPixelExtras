package me.jammy.simppixelextras;

import lombok.Getter;
import me.angeschossen.lands.api.LandsIntegration;
import me.dthbr.utils.config.Cfgs;
import me.jammy.simppixelextras.command.CreateSignatureCmd;
import me.jammy.simppixelextras.command.SignatureCmd;
import me.jammy.simppixelextras.command.SimpPixelCmd;
import me.jammy.simppixelextras.placeholder.SimpPixelExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpPixelExtras extends JavaPlugin {

    @Getter
    private static SimpPixelExtras instance = null;
    @Getter
    private LandsIntegration landsApi = null;
    @Getter
    private Cfgs signatureCfg;
    @Getter
    private Cfgs langCfg;

    private void setupPlaceholderExpansion() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new SimpPixelExpansion(this).register();
    }

    @Override
    public void onLoad() {
        instance = this;

        saveDefaultConfig();
        registerConfigs();
    }

    @Override
    public void onEnable() {
        setupPlaceholderExpansion();

        landsApi = LandsIntegration.of(this);

        this.getCommand("signature").setExecutor(new SignatureCmd(this));
        this.getCommand("createsignature").setExecutor(new CreateSignatureCmd(this));
        this.getCommand("simppixel").setExecutor(new SimpPixelCmd(this));
    }

    private void registerConfigs() {
        signatureCfg = Cfgs.of("signatures.yml");
        langCfg = Cfgs.of("lang.yml");
    }

    @Override
    public void onDisable() {
    }
}
