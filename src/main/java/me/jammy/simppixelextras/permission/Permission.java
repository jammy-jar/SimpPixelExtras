package me.jammy.simppixelextras.permission;

public enum Permission {
    SIGNATURE("signature"),
    ADMIN_SIGNATURE("admin.signature");

    final String perm;

    Permission(final String perm) {
        this.perm = perm;
    }

    public String asPerm() {
        return "simppixel." + perm;
    }
}
