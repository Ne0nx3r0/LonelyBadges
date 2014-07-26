package com.ne0nx3r0.badges.badges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BadgePlayer {
    private final UUID uuid;
    private final HashMap<String, Integer> properties;
    private final List<Badge> badges;
    private boolean dirty;
    
    BadgePlayer(UUID uuid,HashMap<String, Integer> properties,List<Badge> badges) {
        this.uuid = uuid;
        this.properties = new HashMap<>();
        this.badges = new ArrayList<>();
        
        this.dirty = false;
    }
    
    void setProperty(String property, int newValue) {
        if(this.properties.get(property) != newValue){
            this.properties.put(property, newValue);

            this.dirty = true;
        }
    }
    
    void setProperty(String property, int newValue, BadgePropertyCondition bpc) {
        Integer currentValue = this.properties.get(property);
            
        if(bpc == BadgePropertyCondition.ALWAYS){
            if(currentValue != newValue){
                this.properties.put(property, newValue);

                this.dirty = true;
            }
        }
        else if(bpc == BadgePropertyCondition.GREATER_THAN){
            if(currentValue < newValue){
                this.properties.put(property, newValue);

                this.dirty = true;
            }
        }
        else/* if(bpc == BadgePropertyCondition.LESS_THAN)*/{
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
    
    public void grantBadge(Badge badge){
        this.dirty = true;
        
        this.badges.add(badge);
    }
    
    public boolean hasBadge(Badge badge){
        return this.badges.contains(badge);
    }

    public int getProperty(String propertyName) {
        return this.properties.get(propertyName);
    }
    
    public UUID getUniqueId(){
        return this.uuid;
    }
}
