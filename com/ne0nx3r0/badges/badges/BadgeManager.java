package com.ne0nx3r0.badges.badges;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BadgeManager {
    public static final String PROPERTY_PLAYER_MONEY = "lb.player_money";
    public static final String PROPERTY_PLAYER_KILLS = "lb.player_kills";
    
    private final Map<UUID,BadgePlayer> badgePlayers;
    private final List<Badge> badges;
    private final LonelyBadgesPlugin plugin;

    public BadgeManager(LonelyBadgesPlugin plugin){
        this.plugin = plugin;
        
        this.badgePlayers = new HashMap<>();
    
        this.badges = new ArrayList<>();
        
        plugin.getServer().getScheduler().runTaskTimer(plugin, new BadgeAwardTask(plugin,this), 20*10, 20*10);
    }
    
    // Set a property
    public void SetGlobalBadgeProperty(UUID playerId,String property,int value){
        BadgePlayer bp = this.badgePlayers.get(playerId);
        
        if(bp == null){
            bp = this.loadBadgePlayer(playerId);
        }
        
        bp.setProperty(property,value);
    }

    // Set a property but on a condition (typically greater than, meaning greater than the current value)
    public void SetGlobalBadgeProperty(UUID playerId,String property,int value,BadgePropertyCondition bpc){
        BadgePlayer bp = this.badgePlayers.get(playerId);
        
        if(bp == null){
            bp = this.loadBadgePlayer(playerId);
        }
        
        bp.setProperty(property,value,bpc);
    }

    // Adds or substracts to/from an existing property
    public void AdjustGlobalBadgeProperty(UUID playerId,String property,int value){
        BadgePlayer bp = this.badgePlayers.get(playerId);
        
        if(bp == null){
            bp = this.loadBadgePlayer(playerId);
        }
        
        bp.adjustProperty(property,value);
    }
    
    public Badge RegisterBadge(String name,String description,BadgePropertyRequirement[] requirements){
        int simulatedId = 1;
        
        Badge badge = new Badge(simulatedId,name,description,requirements);
        
        this.badges.add(badge);
                
        return badge;
    }

    Iterable<BadgePlayer> getOnlineBadgePlayers() {
        return this.badgePlayers.values();
    }

    Iterable<Badge> getActiveBadges() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public BadgePlayer loadBadgePlayer(UUID uuid){
        HashMap<String, Integer> properties = new HashMap<>();
        List<Badge> badges = new ArrayList<>();
        
        File playerFile = new File(this.plugin.getDataFolder().getAbsolutePath()+"playerBadges",uuid.toString());
        
        System.out.println(playerFile.getAbsolutePath());
        
        if(!playerFile.exists()){
            return new BadgePlayer(uuid,properties,badges);
        }
        
        FileConfiguration playerYml = YamlConfiguration.loadConfiguration(playerFile);
        
        
                
        BadgePlayer badgePlayer = new BadgePlayer(uuid,properties,badges);
            
        this.badgePlayers.put(uuid, badgePlayer);
        
        return badgePlayer;
    }
    
    public void saveBadgePlayer(BadgePlayer bp){
        
    }
}
