package com.ne0nx3r0.badges.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeThing {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getTimeString(Date dt) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        
        return sdf.format(dt.getTime());
    }
    
    public static Date getTimeObj(String sDate) throws ParseException{
        return new SimpleDateFormat(DATE_FORMAT).parse(sDate);
    }
}
