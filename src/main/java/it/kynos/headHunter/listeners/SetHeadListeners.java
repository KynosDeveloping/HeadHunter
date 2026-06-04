package it.kynos.headHunter.listeners;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import it.kynos.headHunter.HeadHunter;
import it.kynos.headHunter.utils.HeadManager;
import it.kynos.kynoslib.utils.ColorUtils;
import it.kynos.kynoslib.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SetHeadListeners implements Listener {

    private final HeadManager headManager;
    private final MessageUtils msg;

    public SetHeadListeners(HeadManager headManager, MessageUtils msg) {
        this.headManager = headManager;
        this.msg = new MessageUtils(HeadHunter.getInstance());
    }

    @EventHandler
    public void onCommandIntercept(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String commandWritten = e.getMessage();

        // Intercepting subcommand string manually to bypass routing constraints if necessary
        if (commandWritten.equalsIgnoreCase("/headhunter sethead")) {
            e.setCancelled(true);
            Block targetBlock = player.getTargetBlockExact(5);

            if (targetBlock == null || (targetBlock.getType() != Material.PLAYER_HEAD && targetBlock.getType() != Material.PLAYER_WALL_HEAD)) {
                msg.send(player, "Messages.HeadHunter.notavalidhead");
                return;
            }
            if (headManager.isHeadRegistered(targetBlock.getLocation())) {
                msg.send(player, "Messages.HeadHunter.alreadyregistered");
                return;
            }

            Skull skull = (Skull) targetBlock.getState();
            PlayerProfile profile = skull.getPlayerProfile();
            String configTextureURL = HeadHunter.getInstance().getConfig().getString("HeadHunters.TextureURL");

            boolean textureMatches = false;

            // Extract and verify base64 texture hashes to ensure authenticity before registration
            if (profile != null && configTextureURL != null) {
                for (ProfileProperty property : profile.getProperties()) {
                    if (property.getName().equals("textures")) {
                        String decoded = new String(java.util.Base64.getDecoder().decode(property.getValue()));
                        if (decoded.contains(configTextureURL)) {
                            textureMatches = true;
                            break;
                        }
                    }
                }
            }

            if (!textureMatches) {
                msg.send(player, "Messages.HeadHunter.errorload");
                return;
            }

            String newId = headManager.registerHead(targetBlock.getLocation());
            String successMsg = HeadHunter.getInstance().getConfig().getString("Messages.HeadHunter.headregistered");
            if (successMsg != null) {
                player.sendMessage(ColorUtils.translateToString(successMsg.replace("%id%", newId)));
            }
        }
    }
}