package com.ne0nx3r0.badges.badges;

import java.util.HashMap;

public class BadgePlayer {
    private final HashMap<String, Integer> properties;
    
    BadgePlayer() {
        
        this.properties = new HashMap<>();
    }
    
    void setProperty(String property, int newValue) {
        this.properties.put(property, newValue);
    }
    
    void setProperty(String property, int newValue, BadgePropertyCondition bpc) {
        if(bpc == BadgePropertyCondition.ALWAYS){
            this.properties.put(property, newValue);
        }
        else {
            Integer currentValue = this.properties.get(property);
            
            if(bpc == BadgePropertyCondition.GREATER_THAN){
                if(currentValue < newValue){
                    this.properties.put(property, newValue);
                }
            }
            else/* if(bpc == BadgePropertyCondition.LESS_THAN)*/{
                if(currentValue > newValue){
                    this.properties.put(property, newValue);
                }
            }
        }
    }

    void adjustProperty(String property, int newValue) {
        Integer currentValue = this.properties.get(property);
        
        this.properties.put(property, currentValue+newValue);
    }
}
