package it.kynos.headHunter.utils;

import it.kynos.headHunter.HeadHunter;
import it.kynos.kynoslib.files.KynosFile;
import java.util.List;

public class PlayerManager extends KynosFile {

    public PlayerManager(HeadHunter plugin, String playerName) {
        super(plugin, "players/" + playerName + ".yml");
    }

    public boolean hasFoundHead(String headId) {
        List<String> foundHeads = getConfig().getStringList("found_heads");
        return foundHeads.contains(headId);
    }

    public void addFoundHead(String headId) {
        List<String> foundHeads = getConfig().getStringList("found_heads");
        if (foundHeads.contains(headId)) return;

        foundHeads.add(headId);
        getConfig().set("found_heads", foundHeads);
        save();
    }

    public int getFoundHeadsCount() {
        return getConfig().getStringList("found_heads").size();
    }
}