package com.ne0nx3r0.badges.badges;

import org.bukkit.Material;

public class Badge {
    private final int id;
    private String name;
    private String description;
    private BadgePropertyRequirement[] bpr;
    private Material material;
    private byte materialData;
    
    public Badge(int id,Material material,byte data,String name,String description,BadgePropertyRequirement[] bpr){
        this.id = id;
        this.material = material;
        this.materialData = data;
        this.name = name;
        this.description = description;
        this.bpr = bpr;
    }
    
    public int getId(){
        return this.id;
    }

    public BadgePropertyRequirement[] getRequirements() {
        return this.bpr;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Material getMaterial() {
        return this.material;
    }

    public byte getMaterialData() {
        return this.materialData;
    }

    void setRequirements(BadgePropertyRequirement[] requirements) {
        this.bpr = requirements;
    }

    void setName(String name) {
        this.name = name;
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setMaterialAndData(Material material,byte materialData) {
        this.material = material;
        this.materialData = materialData;
    }
}
