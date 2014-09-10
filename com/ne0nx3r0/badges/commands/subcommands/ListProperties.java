package com.ne0nx3r0.badges.commands.subcommands;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import com.ne0nx3r0.badges.commands.LonelyCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.command.CommandSender;

public class ListProperties extends LonelyCommand{
    private final LonelyBadgesPlugin plugin;
    
    public ListProperties(LonelyBadgesPlugin plugin) {
        super(
            "listproperties",
            "",
            "View a list of badge properties",
            "lonelybadges.admin.listproperties"
        );
        
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender cs, String[] args) {
        List<String> properties = new ArrayList<>();
        
        Set<Entry<String, String>> entries = this.plugin.getBadgeManager().getAllProperties().entrySet();
        
        for(Entry<String,String> entry : entries){
            properties.add(String.format("%s - %s", new Object[]{entry.getKey(),entry.getValue()}));
        }        
        
        this.send(cs, properties.toArray(new String[entries.size()]));
        
        return true;
    }
}
