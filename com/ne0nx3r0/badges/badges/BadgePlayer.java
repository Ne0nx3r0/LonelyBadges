package com.ne0nx3r0.badges.badges;

import java.util.HashMap;

public class BadgePlayer {
    private final HashMap<String, Integer> properties;
    private boolean dirty;
    
    BadgePlayer() {
        this.properties = new HashMap<>();
        
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
    
    void grantBadge(int BadgeId){
        
    }
    
    public boolean isDirty(){
        return this.dirty;
    }
}
