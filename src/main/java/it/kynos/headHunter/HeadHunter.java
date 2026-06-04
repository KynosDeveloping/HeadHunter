package it.kynos.headHunter;

import it.kynos.headHunter.commands.CommandHead;
import it.kynos.headHunter.listeners.PlayerFindListener;
import it.kynos.headHunter.listeners.SetHeadListeners;
import it.kynos.headHunter.tasks.ActionBarTask;
import it.kynos.headHunter.utils.HeadManager;
import it.kynos.kynoslib.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class HeadHunter extends JavaPlugin {

    private static HeadHunter instance;
    private HeadManager headManager;
    private MessageUtils msg;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.headManager = new HeadManager(this);
        new CommandHead(this, "headhunter");
        getServer().getPluginManager().registerEvents(new SetHeadListeners(headManager, msg), this);
        getServer().getPluginManager().registerEvents(new PlayerFindListener(headManager), this);
        new ActionBarTask(headManager).runTaskTimerAsynchronously(this, 20L, 40L);
        getLogger().info("HeadHunter Started successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("HeadHunter Stopped!");
        if (headManager != null) {
            headManager.save();
        }
    }

    public void reloadPlugin() {
        reloadConfig();
    }

    public MessageUtils msg(){
        return msg;
    }

    public HeadManager getHeadManager(){
        return headManager;
    }

    public static HeadHunter getInstance(){
        return instance;
    }
}