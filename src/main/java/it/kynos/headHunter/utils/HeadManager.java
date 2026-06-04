package it.kynos.headHunter.utils;

import it.kynos.headHunter.HeadHunter;
import it.kynos.kynoslib.files.KynosFile;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class HeadManager extends KynosFile {
    public HeadManager(HeadHunter plugin) {
        super(plugin, "heads.yml");
    }

    public void reloadHeads() {
        reload();
    }

    public boolean isHeadRegistered(Location loc) {
        ConfigurationSection section = getConfig().getConfigurationSection("heads");
        if (section == null) return false;

        for (String key : section.getKeys(false)) {
            String world = getConfig().getString("heads." + key + ".world");
            int x = getConfig().getInt("heads." + key + ".x");
            int y = getConfig().getInt("heads." + key + ".y");
            int z = getConfig().getInt("heads." + key + ".z");

            if (loc.getWorld().getName().equals(world) &&
                    loc.getBlockX() == x &&
                    loc.getBlockY() == y &&
                    loc.getBlockZ() == z) {
                return true;
            }
        }
        return false;
    }

    public String getHeadIdByLocation(Location loc) {
        ConfigurationSection section = getConfig().getConfigurationSection("heads");
        if (section == null) return null;

        for (String key : section.getKeys(false)) {
            String world = getConfig().getString("heads." + key + ".world");
            int x = getConfig().getInt("heads." + key + ".x");
            int y = getConfig().getInt("heads." + key + ".y");
            int z = getConfig().getInt("heads." + key + ".z");

            if (loc.getWorld().getName().equals(world) &&
                    loc.getBlockX() == x &&
                    loc.getBlockY() == y &&
                    loc.getBlockZ() == z) {
                return key;
            }
        }
        return null;
    }

    public boolean removeHeadByLocation(Location loc) {
        ConfigurationSection section = getConfig().getConfigurationSection("heads");
        if (section == null) return false;

        for (String key : section.getKeys(false)) {
            String world = getConfig().getString("heads." + key + ".world");
            int x = getConfig().getInt("heads." + key + ".x");
            int y = getConfig().getInt("heads." + key + ".y");
            int z = getConfig().getInt("heads." + key + ".z");

            if (loc.getWorld().getName().equals(world) &&
                    loc.getBlockX() == x &&
                    loc.getBlockY() == y &&
                    loc.getBlockZ() == z) {

                getConfig().set("heads." + key, null);
                save();
                return true;
            }
        }
        return false;
    }

    public String registerHead(Location loc) {
        int nextId = 1;
        ConfigurationSection section = getConfig().getConfigurationSection("heads");

        if (section != null) {
            int maxId = 0;
            for (String key : section.getKeys(false)) {
                try {
                    int currentId = Integer.parseInt(key);
                    if (currentId > maxId) {
                        maxId = currentId;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            nextId = maxId + 1;
        }

        String idString = String.valueOf(nextId);
        String path = "heads." + idString;
        getConfig().set(path + ".world", loc.getWorld().getName());
        getConfig().set(path + ".x", loc.getBlockX());
        getConfig().set(path + ".y", loc.getBlockY());
        getConfig().set(path + ".z", loc.getBlockZ());
        save();
        return idString;
    }
}