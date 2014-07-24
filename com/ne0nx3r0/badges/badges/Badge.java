package com.ne0nx3r0.badges.badges;

public class Badge {
    private final int id;
    private final String name;
    private final String description;
    private final BadgePropertyRequirement[] bpr;
    
    public Badge(int id,String name,String description,BadgePropertyRequirement[] bpr){
        this.id = id;
        this.name = name;
        this.description = description;
        this.bpr = bpr;
    }
}
