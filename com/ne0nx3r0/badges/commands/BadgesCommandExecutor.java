package com.ne0nx3r0.badges.commands;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import com.ne0nx3r0.badges.commands.subcommands.*;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BadgesCommandExecutor implements CommandExecutor {
    private final HashMap<String, LonelyCommand> commands;
    private final LonelyBadgesPlugin plugin;

    public BadgesCommandExecutor(LonelyBadgesPlugin plugin) {
        this.plugin = plugin;
        
        this.commands = new HashMap<>();
        
        this.registerCommand(new Create(plugin));
        this.registerCommand(new Update(plugin));
        this.registerCommand(new View(plugin));
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
        if(args.length == 0 || args[0].equals("?")) {
            cs.sendMessage("Commands you can use:");

            for(LonelyCommand lc : this.commands.values()) {
                if(cs.hasPermission(lc.getPermissionNode())) {// no need to show them the hat command
                    cs.sendMessage(lc.getUsage());
                }
            }
            
            return true;
        }

        LonelyCommand command = this.commands.get(args[0]);
        
        if(command != null) {
            if(cs.hasPermission(command.getPermissionNode())) {
                if(args.length < 2){
                    return command.execute(cs,new String[]{});
                }
                else {
                    String[] newArgs = new String[args.length-1];
                    
                    // remove first arg
                    for(int i=1;i<args.length;i++){
                        newArgs[i-1] = args[i];
                    }
                    
                    return command.execute(cs,newArgs);
                }
            }
            else {
                command.send(cs, 
                    ChatColor.RED+"You do not have permission to "+command.getAction(),
                    ChatColor.RED+"Required node: "+ChatColor.WHITE+command.getPermissionNode()
                );
            }
        }
        else if(args.length == 1){
            command = this.commands.get("view");
            
            return command.execute(cs, new String[]{args[0]});
        }
        
        cs.sendMessage(ChatColor.RED+"Invalid subcommand: "+args[0]);
        
        return false;
    }
    
    private void registerCommand(LonelyCommand command) {
        this.commands.put(command.getName(), command);
    }
}
