package com.ne0nx3r0.badges.badges;

import com.ne0nx3r0.badges.LonelyBadgesPlugin;

public class BadgeAwardTask implements Runnable{
    private final LonelyBadgesPlugin plugin;
    private final BadgeManager bm;
    
    BadgeAwardTask(LonelyBadgesPlugin plugin, BadgeManager bm) {
        this.plugin = plugin;
        this.bm = bm;
    }

    @Override
    public void run() {
        
    }
    
}
