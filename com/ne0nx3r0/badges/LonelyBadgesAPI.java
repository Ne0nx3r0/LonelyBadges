package com.ne0nx3r0.badges;

import com.ne0nx3r0.badges.badges.BadgeManager;
import com.ne0nx3r0.badges.gui.GuiManager;
import java.util.UUID;

public interface LonelyBadgesAPI {
    public GuiManager getGuiManager();
    
    public BadgeManager getBadgeManager();
    
    public boolean isEnabled();
    
    public void registerBadgeProperty(String propertyName, String propertyDescription);

    public void adjustGlobalBadgeProperty(UUID uuid, String propertyName, int value);

    public void setGlobalBadgeProperty(UUID uuid, String propertyName, int value);
}
