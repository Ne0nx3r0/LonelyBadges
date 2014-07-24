package com.ne0nx3r0.badges.commands.subcommands;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import com.ne0nx3r0.badges.commands.LonelyCommand;
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
            viewBadgesFor = player.getName();
        }
        else {
            viewBadgesFor = args[0];
        }
        
        this.plugin.getGuiManager().openViewBadgesForScreen(cs,viewBadgesFor);
        
        return true;
    }

    @Override
    public String[] getUsage() {
        return new String[]{
            "/badges <username> - View badges for a specific user"
        };
    }
}
