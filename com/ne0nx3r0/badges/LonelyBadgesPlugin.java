package com.ne0nx3r0.badges;

import com.ne0nx3r0.badges.listeners.LonelyBadgesPlayerListener;
import com.ne0nx3r0.badges.commands.BadgesCommandExecutor;
import com.ne0nx3r0.badges.gui.GuiManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

public class LonelyBadgesPlugin extends JavaPlugin{
    private GuiManager gm;
    
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
        
        this.gm = new GuiManager(this);
        
        this.getCommand("badges").setExecutor(new BadgesCommandExecutor(this));
        
        this.getServer().getPluginManager().registerEvents(new LonelyBadgesPlayerListener(this), this);
    }
    
    public GuiManager getGuiManager() {
        return this.gm;
    }

    public void copy(InputStream in, File file) throws IOException
    {
        OutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int len;
        while((len=in.read(buf))>0)
        {
            out.write(buf,0,len);
        }
        out.close();
        in.close();
    }
}
