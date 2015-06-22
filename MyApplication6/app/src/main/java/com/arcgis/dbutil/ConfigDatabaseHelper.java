package com.arcgis.dbutil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.arcgis.httputil.ConstantVar;
import com.arcgis.selectentity.DZSYSDICEntity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfigDatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "confparam.db";
	private static final int DATABASE_VERSION = 1;
    private static final String TAG=ConfigDatabaseHelper.class.getName();

	private static final AtomicInteger usageCounter = new AtomicInteger(0);

	private static ConfigDatabaseHelper helper = null;

	private ConfigDatabaseHelper(Context context) {
        super(context, ConstantVar.DATABASE_PATH+"/"+DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static synchronized ConfigDatabaseHelper getHelper(Context context) {
		if (helper == null) {
			helper = new ConfigDatabaseHelper(context.getApplicationContext());
		}
		usageCounter.incrementAndGet();
		return helper;
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, DZSYSDICEntity.class);
			Log.i(TAG, "created new entries in onCreate: ");
		} catch (SQLException e) {
			Log.e(TAG, "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
		try {
			Log.i(TAG, "onUpgrade");
			TableUtils.dropTable(connectionSource, DZSYSDICEntity.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(TAG, "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		if (usageCounter.decrementAndGet() == 0) {
			super.close();
			helper = null;
		}
	}
}