package strikesclearlag;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.Sound;
import org.bukkit.ChatColor;

public class ClCommand implements CommandExecutor {

    private final StrikesClearlag plugin;

    public ClCommand(StrikesClearlag plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("cl.admin")) {
            sender.sendMessage("§cYou don’t have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(plugin.getConfig().getString("clearlag.messages.help", "§eClear Lag Commands:"));
            sender.sendMessage(plugin.getConfig().getString("clearlag.messages.help_now", "/cl now [world]"));
            sender.sendMessage(plugin.getConfig().getString("clearlag.messages.help_all", "/cl now all"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.getClearlagTask().resetTimerFromConfig();
            sender.sendMessage(ChatColor.GREEN + "[Strikes-Clearlag] Configuration reloaded.");
            return true;
        }

        if (args[0].equalsIgnoreCase("now")) {
            if (args.length < 2) {
                sender.sendMessage(plugin.getConfig().getString("clearlag.messages.invalid_command", "§cInvalid usage."));
                return true;
            }

            if (args[1].equalsIgnoreCase("all")) {
                for (World world : Bukkit.getWorlds()) {
                    clearEntitiesInWorld(world);
                }
                Bukkit.broadcastMessage(plugin.getConfig().getString("clearlag.messages.cleared_all"));
                playSoundToAll("success");
                return true;
            }

            World targetWorld = Bukkit.getWorld(args[1]);
            if (targetWorld != null) {
                clearEntitiesInWorld(targetWorld);
                String msg = plugin.getConfig().getString("clearlag.messages.cleared_world", "Cleared items in %%world%%");
                Bukkit.broadcastMessage(msg.replace("%%world%%", targetWorld.getName()));
                playSoundToAll("success");
                return true;
            } else {
                sender.sendMessage(plugin.getConfig().getString("clearlag.messages.invalid_world", "§cInvalid world."));
                return true;
            }
        }

        sender.sendMessage(plugin.getConfig().getString("clearlag.messages.invalid_command", "§cInvalid usage."));
        return true;
    }

    private void clearEntitiesInWorld(World world) {
        for (Entity entity : world.getEntities()) {
            EntityType type = entity.getType();
            if (type == EntityType.DROPPED_ITEM || type == EntityType.MINECART ||
                    type == EntityType.BOAT || type == EntityType.ARROW || type == EntityType.EXPERIENCE_ORB) {
                entity.remove();
            }
        }
    }

    private void playSoundToAll(String key) {
        String soundName = plugin.getConfig().getString("clearlag.sounds." + key);
        if (soundName == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                Sound sound = Sound.valueOf(soundName.toUpperCase());
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                player.playSound(player.getLocation(), soundName, 1.0f, 1.0f);
            }
        }
    }
}