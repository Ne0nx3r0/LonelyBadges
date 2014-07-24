package com.ne0nx3r0.badges.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class LonelyCommand {
    private final String name;
    private final String usageArguments;
    private final String action;
    private final String permissionNode;
    
    public LonelyCommand(String name,String usageArguments,String action,String permissionNode) {
        this.name = name;
        this.usageArguments = usageArguments;
        this.action = action;
        this.permissionNode = permissionNode;
    }
    
    public String getPermissionNode() {
        return this.permissionNode;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getUsageArguments() {
        return this.usageArguments;
    }

    public boolean execute(CommandSender cs, String[] args) {
        return false;
    }

    public String[] getUsage() {
        return new String[]{
            "/badges "+this.name+" "+this.usageArguments
        };
    }

    public void send(CommandSender cs, String...lines) {
        cs.sendMessage(ChatColor.GRAY+"--- "+ChatColor.DARK_GREEN+this.name+ChatColor.GRAY+" ---");
        
        for(String line : lines) {
            cs.sendMessage(line);
        }
        
        cs.sendMessage("");
    }

    public void sendError(CommandSender cs, String msg) {
        this.send(cs,ChatColor.RED+msg);
    }

    public String getAction() {
        return this.action;
    }
}