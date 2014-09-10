package com.ne0nx3r0.badges.badges;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EarnedBadge {
    private final Badge badge;
    private final Date awardedOn;
    private final String note;
    
    public EarnedBadge(Badge badge,Date awardedOn,String note){
        this.badge = badge;
        this.awardedOn = awardedOn;
        this.note = note;
    }
    
    public Badge getBadge(){
        return this.badge;
    }
    
    public Date getAwardedDate(){
        return this.awardedOn;
    }
    
    public String getNote(){
        return this.note;
    }

    public boolean hasNote() {
        return this.note != null;
    }

    public ItemStack getItem() {
        ItemStack is = new ItemStack(this.badge.getMaterial(),this.badge.getMaterialData());

        ItemMeta meta = is.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD+this.badge.getName());

        List<String> lore = new ArrayList<>();

        if(this.badge.getDescription() != null){
            String[] lines = org.apache.commons.lang.WordUtils.wrap(this.badge.getDescription(), 30, "#!#", true).split("#!#");

            for(String line : lines){
                lore.add(ChatColor.GRAY+line);
            }
        }

        if(this.hasNote()){
            String[] noteLines = org.apache.commons.lang.WordUtils.wrap(this.getNote(), 30, "#!#", true).split("#!#");

            for(String line : noteLines){
                lore.add(ChatColor.RESET+line);
            }
        }
        
        SimpleDateFormat dt = new SimpleDateFormat("EEE, d MMM yyyy HH:mm"); 

        lore.add(ChatColor.DARK_GRAY+dt.format(this.awardedOn));
        
        meta.setLore(lore);

        is.setItemMeta(meta);
        
        return is;
    }
}
