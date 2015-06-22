package com.arcgis.drawtool;

import com.arcgis.entity.MercatorEntity;

import java.util.List;

/**
 * Created by CHENLI on 2015/6/5.
 */
public interface GetPolylineCoordsListener {
    void handleLatLon(List<MercatorEntity> LatLonPoint_List);
}
