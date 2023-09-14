package me.jammy.simppixelextras.permission;

import org.bukkit.permissions.Permissible;

public enum Permission {
    RELOAD("admin.reload"),
    SIGNATURE("signature"),
    ADMIN_SIGNATURE("admin.signature");

    final String perm;

    Permission(final String perm) {
        this.perm = perm;
    }

    private String asString() {
        return "simppixel." + perm;
    }

    public boolean hasPerm(Permissible permissable) {
        return permissable.hasPermission(asString());
    }
}
