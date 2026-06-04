package it.kynos.headHunter.listeners;

import it.kynos.headHunter.HeadHunter;
import it.kynos.headHunter.utils.HeadManager;
import it.kynos.headHunter.utils.PlayerManager;
import it.kynos.kynoslib.utils.ColorUtils;
import it.kynos.kynoslib.utils.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerFindListener implements Listener {

    private final HeadManager headManager;

    public PlayerFindListener(HeadManager headManager) {
        this.headManager = headManager;
    }

    @EventHandler
    public void onHeadClick(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getClickedBlock() == null) return;

        Player player = e.getPlayer();
        String headId = headManager.getHeadIdByLocation(e.getClickedBlock().getLocation());
        if (headId == null) return;

        PlayerManager playerFile = new PlayerManager(HeadHunter.getInstance(), player.getName());

        int totalHeadsInServer = 0;
        ConfigurationSection headsSection = headManager.getConfig().getConfigurationSection("heads");
        if (headsSection != null) {
            totalHeadsInServer = headsSection.getKeys(false).size();
        }
        if (playerFile.getFoundHeadsCount() >= totalHeadsInServer && totalHeadsInServer > 0) {
            String msgAll = HeadHunter.getInstance().getConfig().getString("Messages.Player.alreadyfoundall");
            if (msgAll != null) player.sendMessage(ColorUtils.translateToString(msgAll));
            SoundManager.play(player, "ENTITY_VILLAGER_NO", 1.0f, 1.0f);
            return;
        }
        if (playerFile.hasFoundHead(headId)) {
            String msgFound = HeadHunter.getInstance().getConfig().getString("Messages.Player.alreadyfound");
            if (msgFound != null) player.sendMessage(ColorUtils.translateToString(msgFound));
            SoundManager.play(player, "ENTITY_VILLAGER_NO", 1.0f, 1.0f);
            return;
        }
        playerFile.addFoundHead(headId);
        int foundCount = playerFile.getFoundHeadsCount();
        String msgSingle = HeadHunter.getInstance().getConfig().getString("Messages.Player.headfound");
        if (msgSingle != null) {
            player.sendMessage(ColorUtils.translateToString(msgSingle.replace("%id%", headId)));
        }

        ConfigurationSection barSection = HeadHunter.getInstance().getConfig().getConfigurationSection("Messages.Player.actionbar");
        if (barSection != null && barSection.getBoolean("enabled", true) && totalHeadsInServer > 0) {

            String symbol = barSection.getString("bar_symbol", "■");
            String compColor = barSection.getString("completed_color", "&a");
            String remColor = barSection.getString("remaining_color", "&7");
            StringBuilder barBuilder = new StringBuilder();
            int totalSegments = 10;
            int completedSegments = (int) ((double) foundCount / totalHeadsInServer * totalSegments);
            int remainingSegments = totalSegments - completedSegments;

            barBuilder.append(compColor);
            for (int i = 0; i < completedSegments; i++) {
                barBuilder.append(symbol);
            }
            barBuilder.append(remColor);
            for (int i = 0; i < remainingSegments; i++) {
                barBuilder.append(symbol);
            }

            String actionbarFormat = barSection.getString("format", "&fProgress: %bar% &e%found%&7/&e%total%");
            String finalActionBar = actionbarFormat
                    .replace("%bar%", barBuilder.toString())
                    .replace("%found%", String.valueOf(foundCount))
                    .replace("%total%", String.valueOf(totalHeadsInServer));

            player.sendActionBar(ColorUtils.translateToString(finalActionBar));
        }
        if (foundCount == totalHeadsInServer) {
            String msgWin = HeadHunter.getInstance().getConfig().getString("Messages.Player.completedall");
            if (msgWin != null) {
                player.sendMessage(ColorUtils.translateToString(msgWin.replace("%total%", String.valueOf(totalHeadsInServer))));
            }
            SoundManager.play(player, "UI_TOAST_CHALLENGE_COMPLETE", 1.0f, 1.0f);
            giveFinalReward(player);
        } else {
            SoundManager.play(player, "BLOCK_NOTE_BLOCK_PLING", 1.0f, 1.2f);
        }
    }

    private void giveFinalReward(Player player) {
        ConfigurationSection rewardSection = HeadHunter.getInstance().getConfig().getConfigurationSection("FinalReward");
        if (rewardSection == null) return;

        String type = rewardSection.getString("type", "COMMAND");

        if (type.equalsIgnoreCase("COMMAND")) {
            String cmd = rewardSection.getString("command");
            if (cmd != null && !cmd.isEmpty()) {
                String parsedCmd = cmd.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCmd);
            }
        } else if (type.equalsIgnoreCase("ITEM")) {
            ConfigurationSection itemSec = rewardSection.getConfigurationSection("item");
            if (itemSec == null) return;

            String matName = itemSec.getString("material", "DIAMOND");
            Material material = Material.matchMaterial(matName);
            if (material == null) material = Material.DIAMOND;

            ItemStack rewardItem = new ItemStack(material);
            ItemMeta meta = rewardItem.getItemMeta();

            if (meta != null) {
                if (itemSec.contains("name")) {
                    meta.setDisplayName(ColorUtils.translateToString(itemSec.getString("name")));
                }

                if (itemSec.contains("lore")) {
                    List<String> rawLore = itemSec.getStringList("lore");
                    List<String> coloredLore = new ArrayList<>();
                    for (String line : rawLore) {
                        coloredLore.add(ColorUtils.translateToString(line));
                    }
                    meta.setLore(coloredLore);
                }

                if (itemSec.contains("custom_model_data")) {
                    meta.setCustomModelData(itemSec.getInt("custom_model_data"));
                }

                if (itemSec.contains("enchants")) {
                    for (String enchantStr : itemSec.getStringList("enchants")) {
                        try {
                            String[] split = enchantStr.split(":");
                            String enchantName = split[0].toLowerCase();
                            int level = Integer.parseInt(split[1]);

                            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantName));
                            if (enchantment != null) {
                                meta.addEnchant(enchantment, level, true);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                rewardItem.setItemMeta(meta);
            }
            player.getInventory().addItem(rewardItem);
        }
    }
}