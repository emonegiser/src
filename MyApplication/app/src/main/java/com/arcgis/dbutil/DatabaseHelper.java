package com.arcgis.dbutil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.arcgis.entity.CBYDEntity;
import com.arcgis.entity.DZZHEntity;
import com.arcgis.entity.DZZHinfoEntity;
import com.arcgis.entity.GYPZYDEntity;
import com.arcgis.entity.KCZYEntity;
import com.arcgis.entity.PZYDEntity;
import com.arcgis.entity.SBYDEntity;
import com.arcgis.entity.WPURLEntity;
import com.arcgis.entity.WPZFEntity;
import com.arcgis.entity.XCRWEntity;
import com.arcgis.entity.XCRYEntity;
import com.arcgis.entity.XCSBEntity;
import com.arcgis.httputil.ConstantVar;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private  final static int version=1;

	private static final String DATABASE_NAME = "dzzh.db";
	private static final String TAG=DatabaseHelper.class.getName();

	private static final AtomicInteger usageCounter = new AtomicInteger(0);

	private static DatabaseHelper helper = null;

	private SQLiteDatabase mDefaultWritableDatabase = null;

	private DatabaseHelper(Context context) {
		super(context, ConstantVar.DATABASE_PATH+"/"+DATABASE_NAME, null, version);
//		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static synchronized DatabaseHelper getHelper(Context context) {
		if (helper == null) {
			helper = new DatabaseHelper(context.getApplicationContext());
		}
		usageCounter.incrementAndGet();
		return helper;
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			this.mDefaultWritableDatabase=db;
			TableUtils.createTable(connectionSource, DZZHEntity.class);
			TableUtils.createTable(connectionSource, DZZHinfoEntity.class);
			TableUtils.createTable(connectionSource, XCRYEntity.class);
			TableUtils.createTable(connectionSource, KCZYEntity.class);
			TableUtils.createTable(connectionSource, CBYDEntity.class);
			TableUtils.createTable(connectionSource, GYPZYDEntity.class);
			TableUtils.createTable(connectionSource, PZYDEntity.class);
			TableUtils.createTable(connectionSource, SBYDEntity.class);
			TableUtils.createTable(connectionSource, WPURLEntity.class);
			TableUtils.createTable(connectionSource, WPZFEntity.class);
			TableUtils.createTable(connectionSource, XCRWEntity.class);
			TableUtils.createTable(connectionSource, XCSBEntity.class);

			Log.i(TAG, "created new entries in onCreate: ");
		} catch (SQLException e) {
			Log.e(TAG, "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public ConnectionSource getConnectionSource() {
		return super.getConnectionSource();
	}


	@Override
	public boolean isOpen() {
		return super.isOpen();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(TAG, "onUpgrade");
			this.mDefaultWritableDatabase=db;
			TableUtils.dropTable(connectionSource, DZZHEntity.class, true);
			TableUtils.dropTable(connectionSource,DZZHinfoEntity.class,true);
			TableUtils.dropTable(connectionSource, XCRYEntity.class,true);
			TableUtils.dropTable(connectionSource, KCZYEntity.class, true);
			TableUtils.dropTable(connectionSource, CBYDEntity.class, true);
			TableUtils.dropTable(connectionSource, GYPZYDEntity.class, true);
			TableUtils.dropTable(connectionSource, PZYDEntity.class, true);
			TableUtils.dropTable(connectionSource, SBYDEntity.class, true);
			TableUtils.dropTable(connectionSource, WPURLEntity.class, true);
			TableUtils.dropTable(connectionSource, WPZFEntity.class, true);
			TableUtils.dropTable(connectionSource, XCRWEntity.class, true);
			TableUtils.dropTable(connectionSource, XCSBEntity.class, true);

			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(TAG, "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

    @Override
    public SQLiteDatabase getWritableDatabase() {
        final SQLiteDatabase db;
        if(mDefaultWritableDatabase != null){
            db = mDefaultWritableDatabase;
        } else {
            db = super.getWritableDatabase();
        }
        return db;
    }

    @Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			Log.i(TAG, "onDowngrade");
			this.mDefaultWritableDatabase=db;
			TableUtils.dropTable(connectionSource, DZZHEntity.class, true);
			TableUtils.dropTable(connectionSource,DZZHinfoEntity.class,true);
			TableUtils.dropTable(connectionSource, XCRYEntity.class,true);
			TableUtils.dropTable(connectionSource, KCZYEntity.class, true);
			TableUtils.dropTable(connectionSource, CBYDEntity.class, true);
			TableUtils.dropTable(connectionSource, GYPZYDEntity.class, true);
			TableUtils.dropTable(connectionSource, PZYDEntity.class, true);
			TableUtils.dropTable(connectionSource, SBYDEntity.class, true);
			TableUtils.dropTable(connectionSource, WPURLEntity.class, true);
			TableUtils.dropTable(connectionSource, WPZFEntity.class, true);
			TableUtils.dropTable(connectionSource, XCRWEntity.class, true);
			TableUtils.dropTable(connectionSource, XCSBEntity.class, true);

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