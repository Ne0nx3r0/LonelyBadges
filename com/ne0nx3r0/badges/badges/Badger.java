package com.ne0nx3r0.badges.badges;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Badger {
    private final Map<UUID,BadgePlayer> badgePlayers;

    public Badger(){
        this.badgePlayers = new HashMap<>();
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
}
