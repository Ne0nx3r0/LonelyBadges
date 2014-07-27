package com.ne0nx3r0.badges.commands.subcommands;

import com.ne0nx3r0.badges.badges.Badge;
import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import com.ne0nx3r0.badges.commands.LonelyCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Create extends LonelyCommand{
    private final LonelyBadgesPlugin plugin;
    
    public Create(LonelyBadgesPlugin plugin) {
        super(
            "create",
            "<name w/ spaces>",
            "Create a badge from the item in your hand",
            "lonelybadges.admin.create"
        );
        
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender cs, String[] args) {
        if(!(cs instanceof Player)){
            this.sendError(cs,"Cannot be used from the console.");
            
            return true;
        }
        
        if(args.length < 1){
            this.send(cs, this.getUsage());
            
            return true;
        }
        
        StringBuilder builder = new StringBuilder();
        
        for(int i=0;i<args.length;i++) {
            builder.append(" ").append(args[i]);
        }
        
        String badgeName = builder.substring(1);
        
        Player p = (Player) cs;
        
        ItemStack item = p.getItemInHand();
        
        if(item == null || item.getType().equals(Material.AIR)){
            this.sendError(cs, "You must be holding an item to use for the badge!");
            
            return true;
        }
        
        Material type = item.getType();
        byte data = item.getData().getData();
        
        Badge existingBadge = this.plugin.getBadgeManager().getBadge(badgeName);
        
        if(existingBadge != null){
            this.sendError(cs, badgeName + " already exists!");
            
            return true;
        }
        
        Badge badge = this.plugin.getBadgeManager().createBadge(badgeName,type,data);
        
        this.send(cs,badge.getName()+" created! ID = "+ChatColor.GOLD+badge.getId());
        
        return true;
    }
}
