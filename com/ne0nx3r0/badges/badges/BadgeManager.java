package com.ne0nx3r0.badges.badges;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;
import com.ne0nx3r0.util.TimeThing;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BadgeManager {
    public static final String PROPERTY_PLAYER_MONEY = "lb_player_money";
    public static final String PROPERTY_PLAYER_KILLS = "lb_player_kills";
    public static final String PROPERTY_PLAYER_DEATHS = "lb_player_deaths";
    
    private final Map<UUID,BadgePlayer> onlineBadgePlayers;
    private int badge_cardinality;
    private final List<Badge> activeBadges;
    private boolean badgesAreDirty = false;
    private final LonelyBadgesPlugin plugin;
    private final HashMap<String, String> registeredProperties;

    public BadgeManager(LonelyBadgesPlugin plugin){
        this.plugin = plugin;
        
        // Property,Description
        this.registeredProperties = new HashMap<>();
        
        this.onlineBadgePlayers = new HashMap<>();
    
        this.activeBadges = new ArrayList<>();
        
        this.loadBadges();
        
        plugin.getServer().getScheduler().runTaskTimer(plugin, new BadgeAwardTask(plugin,this), 20*10, 20*10);
        
        
// Save task
        final BadgeManager bm = this;
        
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable(){
            @Override
            public void run() {
                if(badgesAreDirty){
                    bm.saveBadges();
                }
                
                for(BadgePlayer bp : bm.onlineBadgePlayers.values()){
                    if(bp.isDirty()){
                        bm.saveBadgePlayer(bp);
                        
                        bp.setDirty(false);
                    }
                    else if(Bukkit.getServer().getPlayer(bp.getUniqueId()) == null){
                        bm.unloadBadgePlayer(bp.getUniqueId());
                    }
                }
            }
        }, 20*60, 20*60);
    }
    
    public BadgePlayer getBadgePlayer(UUID uuid) {
        BadgePlayer bp = this.onlineBadgePlayers.get(uuid);
        
        if(bp != null){
            bp = this.loadBadgePlayer(uuid);
        }
        
        return bp;        
    }
    
    // Set a property
    public void SetGlobalBadgeProperty(UUID playerId,String property,int value){
        BadgePlayer bp = this.onlineBadgePlayers.get(playerId);
        
        if(bp == null){
            bp = this.loadBadgePlayer(playerId);
        }
        
        bp.setProperty(property,value);
    }

    // Set a property but on a condition (typically greater than, meaning greater than the current value)
    public void SetGlobalBadgeProperty(UUID playerId,String property,int value,BadgePropertyUpdateCondition bpuc){
        BadgePlayer bp = this.onlineBadgePlayers.get(playerId);
        
        if(bp == null){
            bp = this.loadBadgePlayer(playerId);
        }
        
        bp.setProperty(property,value,bpuc);
    }

    // Adds or substracts to/from an existing property
    public void AdjustGlobalBadgeProperty(UUID playerId,String property,int value){
        BadgePlayer bp = this.onlineBadgePlayers.get(playerId);
        
        if(bp == null){
            bp = this.loadBadgePlayer(playerId);
        }
        
        bp.adjustProperty(property,value);
    }
    
    public Badge createBadge(String name,Material material, byte materialData){
        int id = badge_cardinality;
        
        badge_cardinality++;
        
        Badge badge = new Badge(id,material,materialData,name,null,new BadgePropertyRequirement[]{});
        
        this.activeBadges.add(badge);
                
        return badge;
    }

    Iterable<BadgePlayer> getOnlineBadgePlayers() {
        return this.onlineBadgePlayers.values();
    }

    Iterable<Badge> getActiveBadges() {
        return this.activeBadges;
    }
    
    public BadgePlayer loadBadgePlayer(UUID uuid){
        HashMap<String, Integer> properties = new HashMap<>();
        
        List<EarnedBadge> earnedBadges = new ArrayList<>();
        
        File playerFile = new File(this.plugin.getDataFolder().getAbsolutePath()+File.separator+"playerBadges",uuid.toString()+".yml");

        if(!playerFile.exists()){
            BadgePlayer bp = new BadgePlayer(uuid,properties,earnedBadges);
            
            bp.setDirty(true);
            
            this.onlineBadgePlayers.put(uuid, bp);
            
            return bp;
        }
        
        FileConfiguration playerYml = YamlConfiguration.loadConfiguration(playerFile);
        
        if(playerYml.isSet("properties")){
            ConfigurationSection propertiesSection = playerYml.getConfigurationSection("properties");
            
            for(String propertyName : propertiesSection.getKeys(false)){
                properties.put(propertyName, propertiesSection.getInt(propertyName));
            }
        }
        
        if(playerYml.isSet("badges")){
            ConfigurationSection badgesSection = playerYml.getConfigurationSection("badges");

            for(String sBadgeId : badgesSection.getKeys(false)){
                int badgeId = Integer.parseInt(sBadgeId);

                Date awardedOn;

                try {
                    awardedOn = TimeThing.getTimeObj(badgesSection.getString(sBadgeId+".awardedOn"));
                } 
                catch (ParseException ex) {
                    this.plugin.getLogger().log(Level.SEVERE, "Invalid awarded on string for badge id {0} for player {1}", new Object[]{badgeId, uuid.toString()});
                    this.plugin.getLogger().log(Level.SEVERE, null, ex);

                    awardedOn = null;
                }

                String note = badgesSection.getString(sBadgeId+".note");

                for(Badge badge : this.activeBadges){
                    if(badge.getId() == badgeId){
                        earnedBadges.add(new EarnedBadge(badge,awardedOn,note));
                    }
                }
            }
        }
                
        BadgePlayer badgePlayer = new BadgePlayer(uuid,properties,earnedBadges);
            
        this.onlineBadgePlayers.put(uuid, badgePlayer);
        
        return badgePlayer;
    }
    
    public void saveBadgePlayer(BadgePlayer bp){
        File playerFile = new File(this.plugin.getDataFolder().getAbsolutePath()+File.separator+"playerBadges",bp.getUniqueId().toString()+".yml");
        
        if(!playerFile.getParentFile().exists()){
            playerFile.getParentFile().mkdirs();
        }
        
        if(!playerFile.exists()){
            try {
                playerFile.createNewFile();
            } 
            catch (IOException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "Error occurred saving file for {0}!", bp.getUniqueId());
                
                this.plugin.getLogger().log(Level.SEVERE, null, ex);
            }
        }
        
        FileConfiguration playerYml = YamlConfiguration.loadConfiguration(playerFile);
        
        playerYml.set("badges","");
        playerYml.set("properties","");
        
        List<EarnedBadge> allBadges = bp.getAllEarnedBadges();

        for(EarnedBadge eb : allBadges){
            String badgeSection = "badges."+eb.getBadge().getId();
            
            playerYml.set(badgeSection+".awardedOn",TimeThing.getTimeString(eb.getAwardedDate()));
            playerYml.set(badgeSection+".note",eb.getNote());
        }
        
        for(Entry<String,Integer> entry : bp.getAllProperties().entrySet()){
            playerYml.set("properties."+entry.getKey(), entry.getValue());
        }
        
        try {
            playerYml.save(playerFile);
        } catch (IOException ex) {
            this.plugin.getLogger().log(Level.WARNING, "Unable to save badge data for {0}!", bp.getUniqueId());
            
            this.plugin.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    public void unloadBadgePlayer(UUID uniqueId) {
        BadgePlayer bp = this.onlineBadgePlayers.remove(uniqueId);
        
        if(bp != null){
            this.saveBadgePlayer(bp);
        }
    }
    
    private void loadBadges() {
        File badgesFile = new File(this.plugin.getDataFolder(),"badges.yml");
        
        FileConfiguration badgesYml = YamlConfiguration.loadConfiguration(badgesFile);
        
        this.activeBadges.clear();
        
        if(badgesYml.isSet("badges")){
            this.badge_cardinality = badgesYml.getInt("cardinality");
            
            ConfigurationSection badgesSection = badgesYml.getConfigurationSection("badges");

            for(String sBadgeId : badgesSection.getKeys(false)){
                ConfigurationSection badgeSection = badgesSection.getConfigurationSection(sBadgeId);

                int badgeId = Integer.parseInt(sBadgeId);
                String badgeName = badgeSection.getString("name");
                String badgeDescription = badgeSection.getString("description");
                Material material = Material.valueOf(badgeSection.getString("material"));
                byte materialData = Byte.parseByte(badgeSection.getString("materialData"));

                BadgePropertyRequirement[] bpc;
                
                if(badgeSection.isSet("conditions")){
                    ConfigurationSection badgeConditionsSection = badgeSection.getConfigurationSection("conditions");

                    bpc = new BadgePropertyRequirement[badgeConditionsSection.getKeys(false).size()];

                    int i = 0;
                    for(String propertyName : badgeConditionsSection.getKeys(false)){
                        BadgePropertyCondition conditionType = BadgePropertyCondition.valueOf(badgeConditionsSection.getString(propertyName+".condition"));
                        int value = badgeConditionsSection.getInt(propertyName+".value");

                        bpc[i] = new BadgePropertyRequirement(propertyName,conditionType,value);

                        i++;
                    }
                }
                else {
                    bpc = new BadgePropertyRequirement[]{};
                }

                this.activeBadges.add(new Badge(badgeId,material,materialData,badgeName,badgeDescription,bpc));
            }
        }
        else {
            this.badge_cardinality = 1;
        }
    }
    
    private void saveBadges() {
        File badgesFile = new File(this.plugin.getDataFolder(),"badges.yml");
        
        FileConfiguration badgesYml = YamlConfiguration.loadConfiguration(badgesFile);
        
        // reset badges
        badgesYml.set("badges", "");
        
        for(Badge badge : this.activeBadges){
            String badgeSection = "badges."+badge.getId()+".";
            
            badgesYml.set(badgeSection+"name",badge.getName());
            badgesYml.set(badgeSection+"description",badge.getDescription());
            badgesYml.set(badgeSection+"material",badge.getMaterial().toString());
            badgesYml.set(badgeSection+"materialData",badge.getMaterialData());
            
            //reset requirements
             badgesYml.set(badgeSection+"conditions","");
            
            for(BadgePropertyRequirement bpr : badge.getRequirements()){
                String name = bpr.getPropertyName();
                
                badgesYml.set(badgeSection+"conditions."+name+".condition",bpr.getCondition().toString());
                badgesYml.set(badgeSection+"conditions."+name+".value",bpr.getActivationValue());
            }
        }    
        
        try {
            badgesYml.save(badgesFile);
        } catch (IOException ex) {
            this.plugin.getLogger().log(Level.WARNING, "Unable to save badges data!");
            
            this.plugin.getLogger().log(Level.SEVERE, null, ex);
        }
    }

    public void registerProperty(String propertyName, String propertyDescription) {
        this.registeredProperties.put(propertyName,propertyDescription);
    }

    public Badge getBadge(String badgeName) {
        for(Badge badge : this.activeBadges){
            if(badge.getName().equalsIgnoreCase(badgeName)){
                return badge;                
            }
        }
        return null;
    }
    
    public Badge getBadge(int badgeId) {
        for(Badge badge : this.activeBadges){
            if(badge.getId() == badgeId){
                return badge;                
            }
        }
        return null;
    }

    public boolean updateBadgeName(Badge badge, String newName) {
        this.badgesAreDirty = true;
        
        badge.setName(newName);
        
        return true;
    }

    public boolean updateBadgeDescription(Badge badge, String newDescription) {
        this.badgesAreDirty = true;
        
        badge.setDescription(newDescription);
        
        return true;
    }

    public boolean updateBadgeItem(Badge badge, Material type, byte data) {
        this.badgesAreDirty = true;
        
        badge.setMaterialAndData(type, data);
        
        return true;
    }

    public boolean isRegisteredProperty(String propertyName) {
        return this.registeredProperties.containsKey(propertyName);
    }

    public boolean setBadgeCondition(Badge badge, String propertyName, BadgePropertyCondition bpc, int conditionValue) {
        this.badgesAreDirty = true;
        
        BadgePropertyRequirement[] bprs = badge.getRequirements();
        
        // Update the condition if it exists already
        for(int i=0;i<bprs.length;i++){
            if(bprs[i].getPropertyName().equals(propertyName)){
                bprs[i] = new BadgePropertyRequirement(propertyName,bpc,conditionValue);
                
                return true;
            }
        }
        
        BadgePropertyRequirement[] newBprs = new BadgePropertyRequirement[bprs.length+1];
        
        for(int i=0;i<newBprs.length-1;i++){
            newBprs[i] = bprs[i];
        }
        
        newBprs[newBprs.length-1] = new BadgePropertyRequirement(propertyName,bpc,conditionValue);
        
        badge.setRequirements(newBprs);
        
        return true;
    }

    public boolean removeBadgeCondition(Badge badge, String propertyName) {
        BadgePropertyRequirement[] bprs = badge.getRequirements();
        
        // Update the condition if it exists already
        for(int i=0;i<bprs.length;i++){
            if(bprs[i].getPropertyName().equals(propertyName)){
                ArrayUtils.removeElement(bprs, i);
                
                return true;
            }
        }
        
        return false;
    }
}
