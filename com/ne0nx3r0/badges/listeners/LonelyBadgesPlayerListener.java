package com.ne0nx3r0.badges.listeners;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import com.ne0nx3r0.badges.badges.BadgeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class LonelyBadgesPlayerListener implements Listener {
    private final BadgeManager bm;
    //private final LonelyBadgesPlugin plugin;

    public LonelyBadgesPlayerListener(LonelyBadgesPlugin plugin) {
        //this.plugin = plugin;
        
        this.bm = plugin.getBadgeManager();
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerKilled(PlayerDeathEvent e){
        Player pKiller = e.getEntity().getKiller();
        
        if(pKiller != null){
            this.bm.AdjustGlobalBadgeProperty(pKiller.getUniqueId(), BadgeManager.PROPERTY_PLAYER_KILLS, 1);
        }
    }
}
