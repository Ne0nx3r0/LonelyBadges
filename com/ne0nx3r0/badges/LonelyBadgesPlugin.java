package com.ne0nx3r0.badges;

import com.ne0nx3r0.badges.badges.BadgeManager;
import com.ne0nx3r0.badges.listeners.LonelyBadgesPlayerListener;
import com.ne0nx3r0.badges.commands.BadgesCommandExecutor;
import com.ne0nx3r0.badges.gui.GuiManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class LonelyBadgesPlugin extends JavaPlugin{
    private GuiManager gm;
    private BadgeManager bm;
    private Economy economy;
    
    @Override
    public void onEnable(){
        try {
            getDataFolder().mkdirs();

            File configFile = new File(getDataFolder(),"config.yml");

            if(!configFile.exists())
            {
                copy(getResource("config.yml"), configFile);
            }
        } 
        catch (IOException ex) {
            this.getLogger().log(Level.INFO, "Unable to load config!");
            
            return;
        }
        
        Plugin vault = this.getServer().getPluginManager().getPlugin("Vault");
        
        if(vault != null && vault.isEnabled()){
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                this.economy = economyProvider.getProvider();
            }
        }
        
        this.gm = new GuiManager(this);
        
        this.getCommand("badges").setExecutor(new BadgesCommandExecutor(this));
        
        this.bm = new BadgeManager(this);
        
        this.bm.registerProperty(BadgeManager.PROPERTY_PLAYER_KILLS,"Player killed another player");
        this.bm.registerProperty(BadgeManager.PROPERTY_PLAYER_MONEY,"Current money balance from Vault");
        this.bm.registerProperty(BadgeManager.PROPERTY_PLAYER_DEATHS,"Number of player deaths");
        
        this.getServer().getPluginManager().registerEvents(new LonelyBadgesPlayerListener(this), this);
    }
    
    public GuiManager getGuiManager() {
        return this.gm;
    }
    
    public BadgeManager getBadgeManager(){
        return this.bm;
    }
    
    public Economy getEconomy(){
        return this.economy;
    }

    public void copy(InputStream in, File file) throws IOException
    {
        try (OutputStream out = new FileOutputStream(file)) {
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0)
            {
                out.write(buf,0,len);
            }
        }
        in.close();
    }
}
