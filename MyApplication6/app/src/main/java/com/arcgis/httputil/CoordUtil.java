package com.arcgis.httputil;

import com.arcgis.entity.LatLonPoint;
import com.arcgis.entity.MercatorEntity;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

/**
 * Created by mars on 2015/1/30.
 */
public class CoordUtil {
    //墨卡托转经纬度
    public static LatLonPoint Mercator2lonLat(MercatorEntity mercator)
    {
        LatLonPoint lonLat = new LatLonPoint();
        double x = mercator.getX() / 20037508.34 * 180;
        double y = mercator.getY() / 20037508.34 * 180;
        y = 180 / Math.PI * (2 * Math.atan(Math.exp(y * Math.PI / 180)) - Math.PI / 2);
        lonLat.setX(x);
        lonLat.setY(y);
        return lonLat;
    }

//    public static MercatorEntity Lonlat2Mercator(LatLonPoint latLonPoint)
//    {
//        Point MercatorPoint=new Point(latLonPoint.getX(),latLonPoint.getY());
//        Geometry WGS84Point;
//        WGS84Point = GeometryEngine.project(MercatorPoint, SpatialReference.WKID_WGS84_WEB_MERCATOR_AUXILIARY_SPHERE,
//                SpatialReference.WKID_WGS84);
//    }
}
