package net.pl3x.bukkit.ridables.configuration;

import net.pl3x.bukkit.ridables.Ridables;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Lang {
    public static String COMMAND_NO_PERMISSION;

    public static String COLLECT_NO_PERMISSION;
    public static String RIDE_NO_PERMISSION;
    public static String SHOOT_NO_PERMISSION;

    public static String DISABLED_COMMANDS_WHILE_RIDING;

    public static String WATER_BUCKET_NAME;
    public static final List<String> WATER_BUCKET_LORE = new ArrayList<>();

    /**
     * Reload the language file from disk
     */
    public static void reload() {
        Ridables plugin = Ridables.getInstance();
        String langFile = Config.LANGUAGE_FILE;
        File configFile = new File(plugin.getDataFolder(), langFile);
        if (!configFile.exists()) {
            plugin.saveResource(Config.LANGUAGE_FILE, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        COMMAND_NO_PERMISSION = config.getString("command-no-permission", "&cYou do not have permission for that command!");

        COLLECT_NO_PERMISSION = config.getString("collect-no-permission", "&cYou do not have permission to collect dolphins!");
        RIDE_NO_PERMISSION = config.getString("ride-no-permission", "&cYou do not have permission to ride!");
        SHOOT_NO_PERMISSION = config.getString("shoot-no-permission", "&cYou do not have permission to shoot!");

        DISABLED_COMMANDS_WHILE_RIDING = config.getString("disabled-commands-while-riding", "&cCommands are disabled while riding!");

        WATER_BUCKET_NAME = config.getString("bucket-name", "&bBucket of {creature}");
        String lore = config.getString("bucket-lore", "This bucket contains a {creature}!\nRight click in water to place");
        WATER_BUCKET_LORE.clear();
        for (String line : lore.split("\n")) {
            WATER_BUCKET_LORE.add(ChatColor.translateAlternateColorCodes('&', line));
        }
    }

    /**
     * Send a chat message (supports colors and line breaks)
     *
     * @param recipient Receiver of message
     * @param message   Message to send
     */
    public static void send(CommandSender recipient, String message) {
        if (message == null) {
            return; // do not send blank messages
        }
        message = ChatColor.translateAlternateColorCodes('&', message);
        if (ChatColor.stripColor(message).isEmpty()) {
            return; // do not send blank messages
        }

        for (String part : message.split("\n")) {
            recipient.sendMessage(part);
        }
    }
}
