
package com.ne0nx3r0.badges.badges;

class BadgePropertyRequirement {
    final String propertyName;
    final BadgePropertyCondition bpc;
    final int activationValue;
    
    public BadgePropertyRequirement(String propertyName,BadgePropertyCondition bpc,int activationValue){
        this.propertyName = propertyName;
        this.bpc = bpc;
        this.activationValue = activationValue;
    }
}
