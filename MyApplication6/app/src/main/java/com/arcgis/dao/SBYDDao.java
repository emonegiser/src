package com.arcgis.dao;

import android.content.Context;

import com.arcgis.dbutil.DatabaseHelper;
import com.arcgis.entity.SBYDEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mars on 2015/2/12.
 */
public class SBYDDao {

    private Dao<SBYDEntity, Integer> SBYDEntityDaoOpe=null;
    private DatabaseHelper databaseHelper=null;
    private Context context;


    public SBYDDao(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            SBYDEntityDaoOpe = databaseHelper.getDao(SBYDEntity.class);
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
        if(SBYDEntityDaoOpe!=null){
            SBYDEntityDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(SBYDEntity entity){
        try{
            SBYDEntityDaoOpe.create(entity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public SBYDEntity getSBYDEntity(int id){
        SBYDEntity entity = null;
        try{
            entity = SBYDEntityDaoOpe.queryForId(id);

        } catch (SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    public List<SBYDEntity> queryListById(int Id){
        try{
            return SBYDEntityDaoOpe.queryBuilder().where().eq("BH", Id).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<SBYDEntity> queryAll(){
        try{
            return SBYDEntityDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
