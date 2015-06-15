package com.arcgis.dao;

import android.content.Context;

import com.arcgis.dbutil.DatabaseHelper;
import com.arcgis.entity.GYPZYDEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mars on 2015/2/12.
 * 供应用地
 */

public class GYYDDao {

    private Dao<GYPZYDEntity, Integer> GYPZYDEntityDaoOpe=null;
    private DatabaseHelper databaseHelper=null;
    private Context context;

    public GYYDDao(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            GYPZYDEntityDaoOpe = databaseHelper.getDao(GYPZYDEntity.class);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = DatabaseHelper.getHelper(context);
        }
        return databaseHelper;
    }

    public void close(){
        if(GYPZYDEntityDaoOpe!=null){
            GYPZYDEntityDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(GYPZYDEntity entity){
        try{
            GYPZYDEntityDaoOpe.create(entity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public GYPZYDEntity getGYPZYDEntity(int id){
        GYPZYDEntity entity = null;
        try{
            entity = GYPZYDEntityDaoOpe.queryForId(id);

        } catch (SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    public List<GYPZYDEntity> queryListById(int Id){
        try{
            return GYPZYDEntityDaoOpe.queryBuilder().where().eq("BH", Id).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<GYPZYDEntity> queryAll(){
        try{
            return GYPZYDEntityDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
