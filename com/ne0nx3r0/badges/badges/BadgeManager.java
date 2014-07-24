package com.ne0nx3r0.badges.badges;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.scheduler.BukkitTask;

public class BadgeManager {
    private final Map<UUID,BadgePlayer> badgePlayers;
    private final BukkitTask badgeAwardTask;
    private final List<Badge> badges;

    public BadgeManager(LonelyBadgesPlugin plugin){
        this.badgePlayers = new HashMap<>();
    
        this.badges = new ArrayList<>();
        
        this.badgeAwardTask = plugin.getServer().getScheduler().runTaskTimer(plugin, new BadgeAwardTask(plugin,this), 20*10, 20*10);
    }
    
    // Set a property
    public void SetGlobalBadgeProperty(UUID playerId,String property,int value){
        BadgePlayer bp = this.badgePlayers.get(playerId);
        
        if(bp == null){
            bp = new BadgePlayer();
        }
        
        bp.setProperty(property,value);
    }

    // Set a property but on a condition (typically greater than, meaning greater than the current value)
    public void SetGlobalBadgeProperty(UUID playerId,String property,int value,BadgePropertyCondition bpc){
        BadgePlayer bp = this.badgePlayers.get(playerId);
        
        if(bp == null){
            bp = new BadgePlayer();
        }
        
        bp.setProperty(property,value,bpc);
    }

    // Adds or substracts to/from an existing property
    public void AdjustGlobalBadgeProperty(UUID playerId,String property,int value){
        BadgePlayer bp = this.badgePlayers.get(playerId);
        
        if(bp == null){
            bp = new BadgePlayer();
        }
        
        bp.adjustProperty(property,value);
    }
    
    
    
    // DATABASE
    public Badge RegisterBadge(String name,String description,BadgePropertyRequirement[] requirements){
        int simulatedId = 1;
        
        Badge badge = new Badge(simulatedId,name,description,requirements);
        
        this.badges.add(badge);
                
        return badge;
    }
}
