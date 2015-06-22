package com.arcgis.drawtool;


import com.arcgis.entity.MercatorEntity;

import java.util.List;

public interface GetPolygonCoordsListener {

	void handleLatLon(List<MercatorEntity> LatLonPoint_List);
}
