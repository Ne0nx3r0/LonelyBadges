package com.ne0nx3r0.badges.gui;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import com.ne0nx3r0.badges.badges.BadgePlayer;
import com.ne0nx3r0.badges.badges.EarnedBadge;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GuiManager {
    private final LonelyBadgesPlugin plugin;

    public GuiManager(LonelyBadgesPlugin plugin) {
        this.plugin = plugin;
    }

    public void openViewBadgesForScreen(Player pShowInvTo, UUID uuid) {
        BadgePlayer bp = this.plugin.getBadgeManager().getBadgePlayer(uuid);
        
        if(bp.getAllEarnedBadges().isEmpty()){
            pShowInvTo.sendMessage(ChatColor.RED+"No badges to show!");
            
            return;
        }
        
        Inventory inv = this.createViewBadgesInventory(bp);
        
        pShowInvTo.openInventory(inv);
    }
    
    public final String INVENTORY_TITLE = "Badges";
    private final int INVENTORY_SIZE = 54;
    
    public boolean isBadgesInventory(Inventory inv){
        return inv.getTitle().equals(INVENTORY_TITLE);
    }
    
    private Inventory createViewBadgesInventory(BadgePlayer bp) {
        Inventory inv = plugin.getServer().createInventory(null, INVENTORY_SIZE, INVENTORY_TITLE);
        
        int cell = 9;
        
        for(EarnedBadge eb : bp.getAllEarnedBadges()){
            if(cell <= INVENTORY_SIZE){
                inv.setItem(cell, eb.getItem());

                cell++;
            }
            else{
                break;
            }
        }
        
        return inv;
    }
}
