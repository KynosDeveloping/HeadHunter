package it.kynos.headHunter.tasks;

import it.kynos.headHunter.HeadHunter;
import it.kynos.headHunter.utils.HeadManager;
import it.kynos.headHunter.utils.PlayerManager;
import it.kynos.kynoslib.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarTask extends BukkitRunnable {

    private final HeadManager headManager;

    public ActionBarTask(HeadManager headManager) {
        this.headManager = headManager;
    }

    @Override
    public void run() {
        ConfigurationSection barSection = HeadHunter.getInstance().getConfig().getConfigurationSection("Messages.Player.actionbar");
        if (barSection == null || !barSection.getBoolean("enabled", true)) return;

        int totalHeadsInServer = 0;
        ConfigurationSection headsSection = headManager.getConfig().getConfigurationSection("heads");
        if (headsSection != null) {
            totalHeadsInServer = headsSection.getKeys(false).size();
        }
        if (totalHeadsInServer == 0) return;

        String symbol = barSection.getString("bar_symbol", "■");
        String compColor = barSection.getString("completed_color", "&a");
        String remColor = barSection.getString("remaining_color", "&7");
        String actionbarFormat = barSection.getString("format", "&fProgress: %bar% &e%found%&7/&e%total%");

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerManager playerFile = new PlayerManager(HeadHunter.getInstance(), player.getName());
            int foundCount = playerFile.getFoundHeadsCount();

            StringBuilder barBuilder = new StringBuilder();
            int totalSegments = 10;
            int completedSegments = (int) ((double) foundCount / totalHeadsInServer * totalSegments);

            if (completedSegments > totalSegments) completedSegments = totalSegments;
            int remainingSegments = totalSegments - completedSegments;

            barBuilder.append(compColor);
            for (int i = 0; i < completedSegments; i++) {
                barBuilder.append(symbol);
            }
            barBuilder.append(remColor);
            for (int i = 0; i < remainingSegments; i++) {
                barBuilder.append(symbol);
            }

            String finalActionBar = actionbarFormat
                    .replace("%bar%", barBuilder.toString())
                    .replace("%found%", String.valueOf(foundCount))
                    .replace("%total%", String.valueOf(totalHeadsInServer));

            player.sendActionBar(ColorUtils.translateToString(finalActionBar));
        }
    }
}