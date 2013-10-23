
package com.azuisapp.runner.util;

import android.location.Location;

/**
 * 计算两个经纬度之间距离
 * 
 * @author hansontian
 */
public class DistanceUtil {

    /**
     * 计算两个经纬度之间距离
     * 
     * @param longitude1
     * @param latitude1
     * @param longitude2
     * @param latitude2
     * @return 单位为m
     */
    public static double getDistance(double startLongitude, double startLatitude,
            double endLongitude, double endLatitude) {
        float[] results = new float[3];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return results[0];
    }

}
