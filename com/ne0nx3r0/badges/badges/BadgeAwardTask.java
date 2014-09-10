package com.ne0nx3r0.badges.badges;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class BadgeAwardTask implements Runnable{
    private final LonelyBadgesPlugin plugin;
    private final BadgeManager bm;
    private final Economy economy;
    
    BadgeAwardTask(LonelyBadgesPlugin plugin, BadgeManager bm) {
        this.plugin = plugin;
        this.bm = bm;
        this.economy = plugin.getEconomy();
    }
    
    @Override
    public void run() {
        try{
            if(this.economy != null){
                for(Player p : plugin.getServer().getOnlinePlayers()){                    
                    this.bm.SetGlobalBadgeProperty(p.getUniqueId(), BadgeManager.PROPERTY_PLAYER_MONEY, (int) this.economy.getBalance(p.getName()));
                }
            }
        }
        catch(Exception ex){
            this.plugin.getLogger().log(Level.SEVERE, "Economy error occurred!");

            this.plugin.getLogger().log(Level.SEVERE, null, ex);
        }
        
        for(BadgePlayer bp : this.bm.getOnlineBadgePlayers()){
            for(Badge badge : this.bm.getActiveBadges()){
                if(!bp.hasBadge(badge) && badge.getRequirements().length > 0){
                    boolean playerEarnedBadge = true;
                    
                    for(BadgePropertyRequirement bpr : badge.getRequirements()){
                        Integer propertyValue = bp.getProperty(bpr.getPropertyName());
                        
                        // invalid or not set
                        if(propertyValue == null){
                            playerEarnedBadge = false;
                            break;
                        }
                        else if(bpr.getCondition().equals(BadgePropertyCondition.GREATER_THAN)){
                            if(propertyValue <= bpr.getActivationValue()){
                                playerEarnedBadge = false;
                                break;
                            }
                        }
                        else if(bpr.getCondition().equals(BadgePropertyCondition.LESS_THAN)){
                            if(propertyValue >= bpr.getActivationValue()){
                                playerEarnedBadge = false;
                                break;
                            }
                        }
                        else if(bpr.getCondition().equals(BadgePropertyCondition.EQUALS)){
                            if(propertyValue != bpr.getActivationValue()){
                                playerEarnedBadge = false;
                                break;
                            }
                        }
                        else {
                            playerEarnedBadge = false;
                            break;
                        }
                    }
                    
                    if(playerEarnedBadge){
                        this.bm.awardPlayerBadge(bp,badge,null,true);
                    }
                }
            }
        }
    }
    
}
