package com.ne0nx3r0.badges.badges;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import java.util.Date;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
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
        for(BadgePlayer bp : this.bm.getOnlineBadgePlayers()){
            /*try{
                if(this.economy != null){
                    for(Player p : plugin.getServer().getOnlinePlayers()){                    
                        this.bm.SetGlobalBadgeProperty(p.getUniqueId(), BadgeManager.PROPERTY_PLAYER_MONEY, (int) this.economy.getBalance(p.getName()));
                    }
                }
            }
            catch(Exception ex){
                this.plugin.getLogger().log(Level.SEVERE, "Economy error occurred!");
               // this.plugin.getLogger().log(Level.SEVERE, null, ex);
            }*/
            
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
                    }
                    
                    if(playerEarnedBadge){
                        EarnedBadge earnedBadge = new EarnedBadge(badge,new Date(),null);

                        bp.grantBadge(earnedBadge);

                        Player player = plugin.getServer().getPlayer(bp.getUniqueId());

                        plugin.getServer().broadcastMessage(player.getName()+" earned the "+ChatColor.GOLD+badge.getName()+ChatColor.RESET+" badge!");
                    }
                }
            }
        }
    }
    
}
