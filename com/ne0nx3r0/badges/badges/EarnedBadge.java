package com.ne0nx3r0.badges.badges;

import java.util.Date;

public class EarnedBadge {
    private final Badge badge;
    private final Date awardedOn;
    private final String note;
    
    public EarnedBadge(Badge badge,Date awardedOn,String note){
        this.badge = badge;
        this.awardedOn = awardedOn;
        this.note = note;
    }
    
    public Badge getBadge(){
        return this.badge;
    }
    
    public Date getAwardedDate(){
        return this.awardedOn;
    }
    
    public String getNote(){
        return this.note;
    }

    public boolean hasNote() {
        return this.note != null;
    }
}
