/**
 * 
 */
package com.arcgis.httputil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String GetShortDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
	}
    public static String GetLongDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }
}
