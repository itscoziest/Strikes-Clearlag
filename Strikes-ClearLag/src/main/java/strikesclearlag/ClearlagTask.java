package strikesclearlag;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.World;

import java.util.List;

public class ClearlagTask extends BukkitRunnable {

    private final StrikesClearlag plugin;
    private int timer;

    public ClearlagTask(StrikesClearlag plugin) {
        this.plugin = plugin;
        this.timer = plugin.getConfig().getInt("clearlag.clear_time", 600);
    }

    public void resetTimerFromConfig() {
        this.timer = plugin.getConfig().getInt("clearlag.clear_time", 600);
    }

    @Override
    public void run() {
        if (!plugin.getConfig().getBoolean("clearlag.enabled", true)) return;

        timer--;

        List<Integer> warningTimes = plugin.getConfig().getIntegerList("clearlag.warning_times");
        if (warningTimes.contains(timer)) {
            String msg = plugin.getConfig().getString("clearlag.messages.warning", "Clearing in {time}!");
            String formatted = formatTime(timer);
            msg = msg.replace("{time}", formatted);
            Bukkit.broadcastMessage(msg);
            playSoundToAll("countdown");
        }

        if (timer <= 0) {
            Bukkit.broadcastMessage(plugin.getConfig().getString("clearlag.messages.clearing", "Clearing items now!"));
            playSoundToAll("clear");

            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    EntityType type = entity.getType();
                    if (type == EntityType.DROPPED_ITEM || type == EntityType.MINECART ||
                            type == EntityType.BOAT || type == EntityType.ARROW || type == EntityType.EXPERIENCE_ORB) {
                        entity.remove();
                    }
                }
            }

            Bukkit.broadcastMessage(plugin.getConfig().getString("clearlag.messages.done", "Clear complete."));
            playSoundToAll("success");

            List<String> cmds = plugin.getConfig().getStringList("clearlag.post_clear_commands");
            for (Player p : Bukkit.getOnlinePlayers()) {
                for (String cmd : cmds) {
                    String parsed = PlaceholderAPI.setPlaceholders(p, cmd);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
                }
            }

            timer = plugin.getConfig().getInt("clearlag.clear_time", 600);
        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remaining = seconds % 60;
        if (minutes > 0) {
            return minutes + " minute(s) and " + remaining + " second(s)";
        } else {
            return remaining + " second(s)";
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