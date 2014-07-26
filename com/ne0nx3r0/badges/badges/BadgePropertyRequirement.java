
package com.ne0nx3r0.badges.badges;

public class BadgePropertyRequirement {
    private final String propertyName;
    private final BadgePropertyCondition bpc;
    private final int activationValue;
    
    public BadgePropertyRequirement(String propertyName,BadgePropertyCondition bpc,int activationValue){
        this.propertyName = propertyName;
        this.bpc = bpc;
        this.activationValue = activationValue;
    }
    
    public String getPropertyName (){
        return this.propertyName;
    }
    
    public BadgePropertyCondition getCondition(){
        return this.bpc;
    }
    
    public int getActivationValue(){
        return this.activationValue;
    }
}
