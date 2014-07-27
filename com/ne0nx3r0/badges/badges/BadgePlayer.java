package com.ne0nx3r0.badges.badges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BadgePlayer {
    private final UUID uuid;
    private final HashMap<String, Integer> properties;
    private final List<EarnedBadge> earnedBadges;
    private boolean dirty;
    
    BadgePlayer(UUID uuid,HashMap<String, Integer> properties,List<EarnedBadge> earnedBadges) {
        this.uuid = uuid;
        this.properties = properties;
        this.earnedBadges = earnedBadges;
        
        this.dirty = false;
    }
    
    void setProperty(String property, int newValue) {
        if(this.properties.get(property) != newValue){
            this.properties.put(property, newValue);

            this.dirty = true;
        }
    }
    
    void setProperty(String property, int newValue, BadgePropertyUpdateCondition bpc) {
        Integer currentValue = this.properties.get(property);
            
        if(bpc == BadgePropertyUpdateCondition.ALWAYS){
            if(currentValue != newValue){
                this.properties.put(property, newValue);

                this.dirty = true;
            }
        }
        else if(bpc == BadgePropertyUpdateCondition.GREATER_THAN){
            if(currentValue < newValue){
                this.properties.put(property, newValue);

                this.dirty = true;
            }
        }
        else/* if(bpc == BadgePropertyUpdateCondition.LESS_THAN)*/{
            if(currentValue > newValue){
                this.properties.put(property, newValue);

                this.dirty = true;
            }
        }
    }

    void adjustProperty(String property, int newValue) {
        Integer currentValue = this.properties.get(property);
        
        this.properties.put(property, currentValue+newValue);
    }
    
    public boolean isDirty(){
        return this.dirty;
    }
    
    public void grantBadge(EarnedBadge earnedBadge){
        this.dirty = true;
        
        this.earnedBadges.add(earnedBadge);
    }
    
    public boolean hasBadge(Badge badge){
        for(EarnedBadge eb : this.earnedBadges){
            if(eb.getBadge().equals(badge)){
                return true;
            }
        }
        return false;
    }

    public int getProperty(String propertyName) {
        return this.properties.get(propertyName);
    }
    
    public UUID getUniqueId(){
        return this.uuid;
    }
    
    public HashMap<String, Integer> getAllProperties(){
        return this.properties;
    }
    
    public List<EarnedBadge> getAllEarnedBadges(){
        return this.earnedBadges;
    }

    void setDirty(boolean dirtyStatus) {
        this.dirty = dirtyStatus;
    }
}
