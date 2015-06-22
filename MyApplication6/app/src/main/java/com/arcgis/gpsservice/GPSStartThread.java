package com.arcgis.gpsservice;

import android.content.Context;
import android.content.Intent;

public class GPSStartThread extends Thread {

	Context contxt;

	public GPSStartThread(Context contxt) {
		super();
		this.contxt = contxt;
	}

	public void run() {
		Intent intentGPS = new Intent(contxt, GPSService.class);
		contxt.startService(intentGPS);
	}

}