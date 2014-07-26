package com.ne0nx3r0.badges.badges;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class BadgeAwardTask implements Runnable{
    private final LonelyBadgesPlugin plugin;
    private final BadgeManager bm;
    private Economy economy;
    
    BadgeAwardTask(LonelyBadgesPlugin plugin, BadgeManager bm) {
        this.plugin = plugin;
        this.bm = bm;
        this.economy = plugin.getEconomy();
    }

    @Override
    public void run() {
        for(BadgePlayer bp : this.bm.getOnlineBadgePlayers()){
            if(this.economy != null){
                for(Player p : plugin.getServer().getOnlinePlayers()){
                    this.bm.SetGlobalBadgeProperty(p.getUniqueId(), BadgeManager.PROPERTY_PLAYER_MONEY, (int) this.economy.getBalance(p.getName()));
                }
            }
            
            for(Badge badge : this.bm.getActiveBadges()){
                if(!bp.hasBadge(badge)){
                    for(BadgePropertyRequirement bpr : badge.getRequirements()){
                        if(bpr.getCondition().equals(BadgePropertyCondition.GREATER_THAN)){
                            if(bp.getProperty(bpr.getPropertyName()) <= bpr.getActivationValue()){
                                break;
                            }
                        }
                        else if(bpr.getCondition().equals(BadgePropertyCondition.LESS_THAN)){
                            if(bp.getProperty(bpr.getPropertyName()) >= bpr.getActivationValue()){
                                break;
                            }
                        }
                    }

                    bp.grantBadge(badge);
                    
                    Player player = plugin.getServer().getPlayer(bp.getUniqueId());
                    
                    plugin.getServer().broadcastMessage(player.getName()+" has earned the "+badge.getName()+" badge!");
                }
            }
        }
    }
    
}
