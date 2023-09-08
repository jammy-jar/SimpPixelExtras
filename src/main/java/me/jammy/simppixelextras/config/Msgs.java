package me.jammy.simppixelextras.config;

import com.google.common.collect.Lists;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Msgs {

    private final Map<String, Component> componentPlaceholders = new HashMap<>();
    private final Map<String, String> placeholders = new HashMap<>();
    private final String message;

    public static Msgs of(String message) {
        return new Msgs(message);
    }

    private Msgs(String message) {

        if (Cfgs.of("messages").get().isList(message)) {
            List<String> list = Cfgs.of("messages").get(message, Lists.newArrayList());
            this.message = String.join("\n", list);
        } else {
            this.message = Cfgs.of("messages").get(message, message);
        }

    }

    public Msgs vars(Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue();
            this.placeholders.put(entry.getKey(), value);
        }
        return this;
    }

    public Msgs var(String name, Object value) {
        placeholders.put(name, value.toString());
        return this;
    }

    public Msgs cvars(Map<String, Component> placeholders) {
        for (Map.Entry<String, Component> entry : placeholders.entrySet()) {
            Component value = entry.getValue() == null ? Component.empty() : entry.getValue();
            this.componentPlaceholders.put(entry.getKey(), value);
        }
        return this;
    }

    public Msgs cvar(String name, Component value) {
        componentPlaceholders.put(name, value);
        return this;
    }

    public String message() {
        return legacyToMini(message);
    }

    public Component asComp() {
        List<TagResolver> resolvers = Lists.newArrayList();

        placeholders.entrySet()
                .stream()
                .map(e -> Placeholder.parsed(e.getKey(), e.getValue()))
                .forEach(resolvers::add);

        componentPlaceholders.entrySet()
                .stream()
                .map(e -> Placeholder.component(e.getKey(), e.getValue()))
                .forEach(resolvers::add);

        TagResolver resolver = TagResolver.resolver(resolvers);
        String msg = message();
        boolean shouldDisable = !msg.contains("<italic>") && !msg.contains("<em>") && !msg.contains("<i>");

        Component component = MiniMessage
                .miniMessage()
                .deserialize(msg, resolver);
        if (shouldDisable)
            component = component.decoration(TextDecoration.ITALIC, false);
        return component;
    }

    public String asLegacy() {
        return LegacyComponentSerializer
                .builder()
                .character('\u00A7')
                .useUnusualXRepeatedCharacterHexFormat()
                .hexColors()
                .build()
                .serialize(asComp());
    }

    public void send(Audience sender) {
        if (sender != null)
            sender.sendMessage(asComp());
    }

    public void actionBar(Audience sender) {
        if (sender != null)
            sender.sendActionBar(asComp());
    }

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)&([0-9A-FK-ORX])");
    private static final Pattern LEGACY_HEX_PATTERN = Pattern.compile("(?i)&(#[0-9A-F]{6})");

    @SuppressWarnings("deprecation")
    private String legacyToMini(String legacy) {
        Matcher matcher = STRIP_COLOR_PATTERN.matcher(legacy);

        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            char colorChar = matcher.group(1).charAt(0);
            ChatColor color = ChatColor.getByChar(colorChar);
            if (color != null) {
                String colorName = color.getName();
                matcher.appendReplacement(sb, "<" + colorName + ">");
            }
        }

        matcher.appendTail(sb);
        legacy = sb.toString();
        matcher = LEGACY_HEX_PATTERN.matcher(legacy);

        sb = new StringBuilder();
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(sb, "<" + group + ">");
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

}