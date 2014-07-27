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
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BadgeManager {
    public static final String PROPERTY_PLAYER_MONEY = "lb.player_money";
    public static final String PROPERTY_PLAYER_KILLS = "lb.player_kills";
    
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
                }
            }
        }, 20*60, 20*60);
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
        
        File playerFile = new File(this.plugin.getDataFolder().getAbsolutePath()+"playerBadges",uuid.toString()+".yml");
        
        System.out.println(playerFile.getAbsolutePath());
        
        if(!playerFile.exists()){
            return new BadgePlayer(uuid,properties,earnedBadges);
        }
        
        FileConfiguration playerYml = YamlConfiguration.loadConfiguration(playerFile);
        
        ConfigurationSection propertiesSection = playerYml.getConfigurationSection("properties");
        
        for(String propertyName : propertiesSection.getKeys(false)){
            properties.put(propertyName, propertiesSection.getInt(propertyName));
        }
        
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
                
        BadgePlayer badgePlayer = new BadgePlayer(uuid,properties,earnedBadges);
            
        this.onlineBadgePlayers.put(uuid, badgePlayer);
        
        return badgePlayer;
    }
    
    public void saveBadgePlayer(BadgePlayer bp){
        File playerFile = new File(this.plugin.getDataFolder().getAbsolutePath()+"playerBadges",bp.getUniqueId().toString()+".yml");
        
        if(!playerFile.exists()){
            playerFile.mkdirs();
        }
        
        FileConfiguration playerYml = YamlConfiguration.loadConfiguration(playerFile);
        
        List<EarnedBadge> allBadges = bp.getAllEarnedBadges();

        for(EarnedBadge eb : allBadges){
            String badgeSection = "badges."+eb.getBadge().getId();
            
            playerYml.set(badgeSection+".awardedOn",eb.getAwardedDate());
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

                ConfigurationSection badgeConditionsSection = badgeSection.getConfigurationSection("conditions");
                BadgePropertyRequirement[] bpc = new BadgePropertyRequirement[badgeConditionsSection.getKeys(false).size()];
                int i = 0;
                for(String propertyName : badgeConditionsSection.getKeys(false)){
                    BadgePropertyCondition conditionType = BadgePropertyCondition.valueOf(badgeConditionsSection.getString("condition"));
                    int value = badgeConditionsSection.getInt("value");

                    bpc[i] = new BadgePropertyRequirement(propertyName,conditionType,value);

                    i++;
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
            
            for(BadgePropertyRequirement bpr : badge.getRequirements()){
                String name = bpr.getPropertyName();
                
                badgesYml.set(badgeSection+"conditions."+name+".condition",bpr.getCondition());
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
