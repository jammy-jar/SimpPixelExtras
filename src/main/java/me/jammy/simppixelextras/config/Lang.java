package me.jammy.simppixelextras.config;

import me.jammy.simppixelextras.SimpPixelExtras;

public enum Lang {
    RELOAD_SUCCESSFUL("main.reload-successful"),
    INSUFFICIENT_PERMISSIONS("main.insufficient-permissions"),
    INVALID_FORMAT_FIRST("main.invalid-format.first"),
    INVALID_FORMAT_SYNTAX("main.invalid-format.syntax"),
    UNKNOWN_COMMAND("main.unknown-command"),
    SIG_CREATED_SUCCESS("create-signature-cmd.sig-created"),
    SIG_CONSOLE_ERR("signature-cmd.console-err"),
    NO_EXISTING_SIG("signature-cmd.no-existing-sig"),
    SIG_AIR_ERR("signature-cmd.sig-air-err"),
    SIG_APPLIED_SUCCESS("signature-cmd.sig-applied-success"),
    SIG_APPLIED_BROADCAST("signature-cmd.sig-applied-broadcast");

    final String path;

    Lang(final String path) {
        this.path = path;
    }

    public String getLang() {
        return SimpPixelExtras.getInstance().getLangCfg().get().getString(path);
    }

}
