package com.ne0nx3r0.badges.commands.subcommands;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import com.ne0nx3r0.badges.badges.Badge;
import com.ne0nx3r0.badges.badges.BadgePropertyCondition;
import com.ne0nx3r0.badges.badges.BadgePropertyRequirement;
import com.ne0nx3r0.badges.commands.LonelyCommand;
import org.bukkit.Material;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Update extends LonelyCommand{
    private final LonelyBadgesPlugin plugin;
    
    public Update(LonelyBadgesPlugin plugin) {
        super(
            "update",
            "<badgeId> <name|description|item|addCondition|removeCondition> [new data]",
            "Update a badge",
            "lonelybadges.admin.update"
        );
        
        this.plugin = plugin;
    }

    @Override
    public String[] getUsage(){
        return new String[]{
            "/badges update <badgeId> name <name w/ spaces>",
            "/badges update <badgeId> description  <desc w/ spaces>",
            "/badges update <badgeId> item",
            "/badges update <badgeId> setCondition <propertyName> <gt/lt/eq> <value>",
            "/badges update <badgeId> removeCondition <propertyName>"
        };
    }
    
    @Override
    public boolean execute(CommandSender cs, String[] args) {
        if(!(cs instanceof Player)){
            this.sendError(cs,"Cannot be used from the console.");
            
            return true;
        }
        
        if(args.length < 2){
            this.send(cs, this.getUsage());
            
            return true;
        }
        
        String sBadgeId = args[0];
        
        int badgeId = -1;
        
        try{
            badgeId = Integer.parseInt(sBadgeId);
        }
        catch(Exception ex){
            this.sendError(cs, sBadgeId+" is not a valid number!");
            
            return true;
        }
        
        String action = args[1];
        
        Badge badge = this.plugin.getBadgeManager().getBadge(badgeId);
        
        if(badge == null){
            this.sendError(cs, sBadgeId+" is not a valid badge ID!");
            
            return true;
        }
        
        if(action.equalsIgnoreCase("name")){
            if(args.length < 3){
                this.sendError(cs, "You must supply a new name!");
                
                return true;
            }
            
            String newName = this.concatArgs(args);
            
            if(this.plugin.getBadgeManager().updateBadgeName(badge,newName)){
                this.send(cs, "Updated the name for "+badge.getId()+" to "+newName);
            }
            else {
                this.sendError(cs, "An error occurred!");
            }
        }
        else if(action.equalsIgnoreCase("description")){
            if(args.length < 3){
                this.sendError(cs, "You must supply a new description!");
                
                return true;
            }
            
            String newDescription = this.concatArgs(args);
            
            if(this.plugin.getBadgeManager().updateBadgeDescription(badge,newDescription)){
                this.send(cs, "Updated the description for "+badge.getName()+" to "+newDescription);
            }
            else {
                this.sendError(cs, "An error occurred!");
            }
        }
        else if(action.equalsIgnoreCase("item")){
            Player player = (Player) cs;
            
            ItemStack item = player.getItemInHand();
            
            if(item == null || item.getType().equals(Material.AIR)){
                this.sendError(cs, "You must be holding an item!");
                
                return true;
            }
            
            if(this.plugin.getBadgeManager().updateBadgeItem(badge,item.getType(),item.getData().getData())){
                this.send(cs, "Updated the item for "+badge.getName());
            }
            else {
                this.sendError(cs, "An error occurred!");
            }
        }
        else if(action.equalsIgnoreCase("setCondition")){
            if(args.length < 4){
                this.send(cs,"/badges update <badgeId> setCondition <propertyName> <gt/lt/eq> <value>");
                
                return true;
            }
            
            String propertyName = args[2];
            
            if(!this.plugin.getBadgeManager().isRegisteredProperty(propertyName)){
                this.sendError(cs, propertyName+" is not a registered badge property!");
                
                return true;
            }
            
            String sConditionType = args[3];
            BadgePropertyCondition bpc;
            
            switch(sConditionType){
                default:
                    this.sendError(cs,sConditionType+" is not a valid condition type! (must be eq/gt/lt)");
                    return true;
                case "gt":
                    bpc = BadgePropertyCondition.GREATER_THAN;
                    break;
                case "eq":
                    bpc = BadgePropertyCondition.EQUALS;
                    break;
                case "lt":
                    bpc = BadgePropertyCondition.LESS_THAN;
                    break;
            }
            
            String sConditionValue = args[4];
            int conditionValue = -1;
            
            try{
                conditionValue = Integer.parseInt(sConditionValue);
            }
            catch(NumberFormatException ex){
                this.sendError(cs, sConditionValue+" is not a valid number!");
            }

            if(this.plugin.getBadgeManager().setBadgeCondition(badge,propertyName,bpc,conditionValue)){
                this.send(cs, "Set condition ("+propertyName+" "+sConditionType+" "+conditionValue+") to "+badge.getName()+"!");
            }
            else {
                this.sendError(cs, "An error occurred!");
            }
        }
        else if(action.equalsIgnoreCase("removeCondition")){
            if(args.length < 3){
                this.send(cs,"/badges update <badgeId> removeCondition <propertyName>");
                
                return true;
            }
            
            String propertyName = args[2];
            
            for(BadgePropertyRequirement bpr : badge.getRequirements()){
                if(bpr.getPropertyName().equals(propertyName)){
                    if(this.plugin.getBadgeManager().removeBadgeCondition(badge,propertyName)){
                        this.send(cs, "Removed "+propertyName+" from "+badge.getName());
                    }
                    else {
                        this.sendError(cs, badge.getName()+"does not appear to have "+propertyName+" as a condition!");
                    }
                    
                    return true;
                }
            }
            
            this.sendError(cs, badge.getName()+" does not have property "+propertyName+" as a condition!");
        }
        else {
            this.sendError(cs, "Invalid action!");
        }
        
        return true;
    }
    
    private String concatArgs(String[] args){
        StringBuilder builder = new StringBuilder();
        
        for(int i=2;i<args.length;i++) {
            builder.append(" ").append(args[i]);
        }
        
        return builder.substring(1);
    }
}
