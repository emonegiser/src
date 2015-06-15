package com.arcgis.dao;

import android.content.Context;

import com.arcgis.dbutil.DatabaseHelper;
import com.arcgis.entity.PZYDEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mars on 2015/2/12.
 */
public class PZYDDao {

    private Dao<PZYDEntity, Integer> PZYDEntityDaoOpe=null;
    private DatabaseHelper databaseHelper=null;
    private Context context;

    private  final static int version=1;

    public PZYDDao(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            PZYDEntityDaoOpe = databaseHelper.getDao(PZYDEntity.class);
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
        if(PZYDEntityDaoOpe!=null){
            PZYDEntityDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(PZYDEntity entity){
        try{
            PZYDEntityDaoOpe.create(entity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public PZYDEntity getPZYDEntity(int id){
        PZYDEntity entity = null;
        try{
            entity = PZYDEntityDaoOpe.queryForId(id);

        } catch (SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    public List<PZYDEntity> queryListById(int Id){
        try{
            return PZYDEntityDaoOpe.queryBuilder().where().eq("BH", Id).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<PZYDEntity> queryAll(){
        try{
            return PZYDEntityDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
