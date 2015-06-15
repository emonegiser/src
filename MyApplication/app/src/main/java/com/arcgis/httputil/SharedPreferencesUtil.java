package com.arcgis.httputil;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferencesUtil {
	
	SharedPreferences sp;
	
	public SharedPreferencesUtil(SharedPreferences sp){
		this.sp=sp;
	}

	public void write(String key,String value){
		SharedPreferences.Editor editor=sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public Map read(String key){
		Map<String, Object> map=new HashMap<String, Object>();	
		map.put(key, sp.getString(key, null));
		return map;
	}

}
