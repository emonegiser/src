package com.arcgis.gpsservice;

import android.content.Context;
import android.content.Intent;

public class GPSStopThread extends Thread {
	
	Context contxt;
	
	public GPSStopThread(Context contxt) {
		super();
		this.contxt = contxt;
	}

	public void run() {
		Intent intentGPS = new Intent(contxt, GPSService.class);
		contxt.stopService(intentGPS);
	}

}
