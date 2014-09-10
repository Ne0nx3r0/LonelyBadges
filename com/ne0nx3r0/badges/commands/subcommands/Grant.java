package com.ne0nx3r0.badges.commands.subcommands;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import com.ne0nx3r0.badges.badges.Badge;
import com.ne0nx3r0.badges.badges.BadgePlayer;
import com.ne0nx3r0.badges.commands.LonelyCommand;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Grant extends LonelyCommand{
    private final LonelyBadgesPlugin plugin;
    
    public Grant(LonelyBadgesPlugin plugin) {
        super(
            "grant",
            "<username> <badgeId>",
            "Grant a user a badge",
            "lonelybadges.admin.grant"
        );
        
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender cs, String[] args) {
        if(args.length < 2){
            this.send(cs,this.getUsage());
            
            return true;
        }
        
        String sBadgeId = args[1];
        
        int badgeId = -1;
        
        try{
            badgeId = Integer.parseInt(sBadgeId);
        }
        catch(NumberFormatException ex){
            this.sendError(cs, sBadgeId+ "is not a valid badge id!");
            
            return true;
        }
        
        Badge badge = this.plugin.getBadgeManager().getBadge(badgeId);
        
        if(badge == null){
            
            this.sendError(cs, sBadgeId+ "is not a valid badge id!");
            
            return true;
        }
        
        String playerName = args[0];
        
        Player p = this.plugin.getServer().getPlayer(playerName);
        UUID uuid;
        
        if(p != null){
            uuid = p.getUniqueId();
        }
        else {
            OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(playerName);
            
            if(offlinePlayer == null){
                this.sendError(cs,"Invalid player!");
                
                return true;
            }
            
            uuid = offlinePlayer.getUniqueId();
        }
        
        BadgePlayer badgePlayer = this.plugin.getBadgeManager().getBadgePlayer(uuid);
        
        if(badgePlayer.hasBadge(badge)){
            this.sendError(cs, playerName+" already has #"+sBadgeId+" ("+badge.getName()+")");
            
            return true;
        }
        
        StringBuilder sb = new StringBuilder();
        
        String note = null;
        
        for(int i=2;i<args.length;i++){
            sb.append(" ").append(args[i]);
        }
        
        if(sb.length() > 0){
            note = sb.substring(1);
        }
        
        this.plugin.getBadgeManager().awardPlayerBadge(badgePlayer, badge, note, true);
        
        return true;
    }

    @Override
    public String[] getUsage() {
        return new String[]{
            "/badges <username> - View badges for a specific user"
        };
    }
}
