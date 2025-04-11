package strikesclearlag;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class StrikesClearlag extends JavaPlugin {

    private ClearlagTask clearLagTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("cl").setExecutor(new ClCommand(this));

        clearLagTask = new ClearlagTask(this);
        clearLagTask.runTaskTimer(this, 20L, 20L); // every second

        getLogger().info("Strikes-Clearlag has been enabled.");
    }

    public ClearlagTask getClearlagTask() {
        return clearLagTask;
    }

    @Override
    public void onDisable() {
        getLogger().info("Strikes-Clearlag has been disabled.");
    }
}
