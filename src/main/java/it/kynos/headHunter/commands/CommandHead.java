package it.kynos.headHunter.commands;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import it.kynos.headHunter.HeadHunter;
import it.kynos.headHunter.utils.HeadManager;
import it.kynos.kynoslib.commands.KynosCommand;
import it.kynos.kynoslib.utils.ColorUtils;
import it.kynos.kynoslib.utils.MessageUtils;
import it.kynos.kynoslib.utils.SoundManager;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class CommandHead extends KynosCommand {
    private final MessageUtils msg = new MessageUtils(plugin);

    public CommandHead(HeadHunter plugin, String headhunter) {
        super(plugin, "headhunter");
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args){
        msg.sendList(sender, "Messages.HeadHunter.help");
        if (sender instanceof Player player) {
            SoundManager.play(player, "BLOCK_NOTE_BLOCK_PLING", 1.0f, 1.0f);
        }
        return true;
    }

    @SubCommand(name = "info", permission = "headhunter.player.info", playerOnly = false)
    public boolean onInfo(CommandSender sender, String[] args) {
        // PLEASE KEEP MY NAME IN THE AUTHORS SECTION, CONSIDERING I RELEASED THIS FOR FREE.
        sender.sendMessage(ColorUtils.translateToString("&8&m-------------------------------------------------"));
        sender.sendMessage(ColorUtils.translateToString("  &c&lHEADHUNTER &7• &fInformation"));
        sender.sendMessage(ColorUtils.translateToString(""));
        sender.sendMessage(ColorUtils.translateToString("  &7» &fVersion: &e" + HeadHunter.getInstance().getDescription().getVersion()));
        sender.sendMessage(ColorUtils.translateToString("  &7» &fAuthor: &a" + String.join(", ", HeadHunter.getInstance().getDescription().getAuthors())));
        sender.sendMessage(ColorUtils.translateToString("  &7» &fPlatform: &bPaper/Purpur 1.21+"));
        sender.sendMessage(ColorUtils.translateToString(""));
        sender.sendMessage(ColorUtils.translateToString("&8&m-------------------------------------------------"));

        if (sender instanceof Player player) {
            SoundManager.play(player, "BLOCK_NOTE_BLOCK_PLING", 1.0f, 1.3f);
        }
        return true;
    }

    @SubCommand(name = "reloadconfig", permission = "headhunter.admin.reloadconfig", playerOnly = false)
    public boolean onReloadConfig(CommandSender sender, String[] args){
        HeadHunter.getInstance().reloadPlugin();
        if (sender instanceof Player player) {
            player.sendTitle(ColorUtils.translateToString("&aConfig.yml"), ColorUtils.translateToString("&fReloaded successfully!"));
            SoundManager.play(player, "BLOCK_NOTE_BLOCK_PLING", 1.0f, 1.5f);
        } else {
            sender.sendMessage("§a[HeadHunter] Config.yml successfully reloaded from disk!");
        }
        return true;
    }

    @SubCommand(name = "givehead", permission = "headhunter.admin.givehead", playerOnly = true)
    public boolean onGiveHead(CommandSender sender, String[] args){
        Player player = (Player) sender;

        if (args.length < 1) {
            msg.send(sender, "Messages.HeadHunter.giveheadqta");
            SoundManager.play(player, "ENTITY_VILLAGER_NO", 1.0f, 1.0f);
            return true;
        }

        int amount = 0;
        try {
            amount = Integer.parseInt(args[0]);
            if (amount <= 0) {
                msg.send(sender, "Messages.HeadHunter.giveheadqtaupper");
                SoundManager.play(player, "ENTITY_VILLAGER_NO", 1.0f, 1.0f);
                return true;
            }
        } catch (NumberFormatException e) {
            msg.send(sender, "Messages.HeadHunter.invalidvalue");
            SoundManager.play(player, "ENTITY_VILLAGER_NO", 1.0f, 1.0f);
            return true;
        }

        ItemStack treasureHead = new ItemStack(Material.PLAYER_HEAD, amount);
        SkullMeta skullMeta = (SkullMeta) treasureHead.getItemMeta();

        if (skullMeta != null) {
            skullMeta.setDisplayName(ColorUtils.translateToString(HeadHunter.getInstance().getConfig().getString("HeadHunters.name")));
            List<String> rawLore = HeadHunter.getInstance().getConfig().getStringList("HeadHunters.lore");
            List<String> coloredLore = rawLore.stream()
                    .map(ColorUtils::translateToString)
                    .toList();
            skullMeta.setLore(coloredLore);

            String textureURL = HeadHunter.getInstance().getConfig().getString("HeadHunters.TextureURL");
            if (textureURL != null && !textureURL.isEmpty()) {
                try {
                    PlayerProfile profile = org.bukkit.Bukkit.createProfile(UUID.randomUUID(), "HeadHunters");
                    String encodedData = Base64.getEncoder().encodeToString(("{textures:{SKIN:{url:\"" + textureURL + "\"}}}").getBytes());
                    ProfileProperty property = new ProfileProperty("textures", encodedData);

                    profile.setProperty(property);
                    skullMeta.setPlayerProfile(profile);
                } catch (Exception e) {
                    msg.send(sender, "Messages.HeadHunter.errorload");
                    String errorConsole = HeadHunter.getInstance().getConfig().getString("Messages.HeadHunter.errorload");
                    HeadHunter.getInstance().getLogger().warning(ColorUtils.translateToString(errorConsole));
                    SoundManager.play(player, "ENTITY_ITEM_BREAK", 1.0f, 0.5f);
                }
            }
            treasureHead.setItemMeta(skullMeta);
        }

        player.getInventory().addItem(treasureHead);
        msg.send(player, "Messages.HeadHunter.headgivved");
        SoundManager.play(player, "ENTITY_ITEM_PICKUP", 1.0f, 1.0f);
        return true;
    }

    @SubCommand(name = "sethead", permission = "headhunter.admin.sethead", playerOnly = true)
    public boolean onSetHead(CommandSender sender, String[] args){
        // The core logic of sethead is handled inside SetHeadListeners.java
        return true;
    }

    @SubCommand(name = "reloadheads", permission = "headhunter.admin.reloadheads", playerOnly = false)
    public boolean onReloadHead(CommandSender sender, String[] args){
        HeadHunter.getInstance().getHeadManager().reloadHeads();
        if (sender instanceof Player player) {
            player.sendTitle(ColorUtils.translateToString("&aHeads Config"), ColorUtils.translateToString("&fReloaded successfully!"));
            SoundManager.play(player, "BLOCK_NOTE_BLOCK_PLING", 1.0f, 1.5f);
        } else {
            sender.sendMessage("§a[HeadHunter] Heads configuration successfully reloaded from disk!");
        }
        return true;
    }

    @SubCommand(name = "removehead", permission = "headhunter.admin.removehead", playerOnly = true)
    public boolean onRemoveHead(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        org.bukkit.block.Block targetBlock = player.getTargetBlockExact(5);

        if (targetBlock == null || (targetBlock.getType() != Material.PLAYER_HEAD && targetBlock.getType() != Material.PLAYER_WALL_HEAD)) {
            msg.send(player, "Messages.HeadHunter.notavalidhead");
            SoundManager.play(player, "ENTITY_VILLAGER_NO", 1.0f, 1.0f);
            return true;
        }

        HeadManager manager = HeadHunter.getInstance().getHeadManager();
        if (manager.removeHeadByLocation(targetBlock.getLocation())) {
            msg.send(player, "Messages.HeadHunter.headremoved");
            SoundManager.play(player, "ENTITY_VILLAGER_YES", 1.0f, 1.0f);
        } else {
            msg.send(player, "Messages.HeadHunter.notregistered");
            SoundManager.play(player, "ENTITY_VILLAGER_NO", 1.0f, 1.0f);
        }

        return true;
    }
}