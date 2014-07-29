package com.ne0nx3r0.badges.commands.subcommands;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import com.ne0nx3r0.badges.commands.LonelyCommand;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class View extends LonelyCommand{
    private final LonelyBadgesPlugin plugin;
    
    public View(LonelyBadgesPlugin plugin) {
        super(
            "view",
            "[username]",
            "View badges for yourself or another user",
            "lonelybadges.view"
        );
        
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender cs, String[] args) {
        if(!(cs instanceof Player)){
            this.sendError(cs,"Cannot view badges from the console.");
            
            return true;
        }
        
        Player player = (Player) cs;
        
        String viewBadgesFor = "";
        
        if(args.length == 0){
            this.plugin.getGuiManager().openViewBadgesForScreen(player,player.getUniqueId());
        }
        else {
            viewBadgesFor = args[0];
        
            Player pViewBadgesFor = this.plugin.getServer().getPlayer(viewBadgesFor);

            UUID uuidBadgesFor;

            if(pViewBadgesFor != null){
                uuidBadgesFor = pViewBadgesFor.getUniqueId();
            }
            else{
                uuidBadgesFor = this.plugin.getServer().getOfflinePlayer(viewBadgesFor).getUniqueId();
            }

            this.plugin.getGuiManager().openViewBadgesForScreen(player,uuidBadgesFor);
        }
        
        return true;
    }

    @Override
    public String[] getUsage() {
        return new String[]{
            "/badges <username> - View badges for a specific user"
        };
    }
}
