package io.aurora;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DropCleaner extends JavaPlugin {

    private boolean enabled;
    private int interval;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        if (enabled) {
            startClearingTask();
        }
        getLogger().info("DropCleaner has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("DropCleaner has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("dcer")) {
            if (args.length == 0) {
                sender.sendMessage("Usage: /dcer <enable|disable|reload>");
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "enable":
                    enabled = true;
                    getConfig().set("enabled", true);
                    saveConfig();
                    startClearingTask();
                    sender.sendMessage("DropCleaner has been enabled.");
                    break;
                case "disable":
                    enabled = false;
                    getConfig().set("enabled", false);
                    saveConfig();
                    sender.sendMessage("DropCleaner has been disabled.");
                    break;
                case "reload":
                    reloadConfig();
                    loadConfig();
                    sender.sendMessage("DropCleaner configuration reloaded.");
                    break;
                default:
                    sender.sendMessage("Usage: /dcer <enable|disable|reload>");
                    break;
            }
            return true;
        }
        return false;
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        enabled = config.getBoolean("enabled", true);
        interval = config.getInt("interval", 600);
    }

    private void startClearingTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (enabled) {
                    int itemCount = 0;
                    for (org.bukkit.World world : Bukkit.getWorlds()) {
                        for (Entity entity : world.getEntities()) {
                            if (entity instanceof Item) {
                                entity.remove();
                                itemCount++;
                            }
                        }
                    }
                    Bukkit.getLogger().info("\u001B[33m[幻梦娘]\u001B[0m"+ "清理了" + itemCount + "个凋落物！");
                }
            }
        }.runTaskTimer(this, 0, interval * 20L);
    }
}
