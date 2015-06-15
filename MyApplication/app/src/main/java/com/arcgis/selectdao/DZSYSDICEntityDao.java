package com.arcgis.selectdao;

import android.content.Context;

import com.arcgis.dbutil.ConfigDatabaseHelper;
import com.arcgis.selectentity.DZSYSDICEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mars on 2015/2/13.
 */
public class DZSYSDICEntityDao {
    private Dao<DZSYSDICEntity, Integer> DZSYSDICEntityDaoOpe=null;
    private ConfigDatabaseHelper databaseHelper=null;
    private Context context;

    public DZSYSDICEntityDao(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            DZSYSDICEntityDaoOpe = databaseHelper.getDao(DZSYSDICEntity.class);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private ConfigDatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = ConfigDatabaseHelper.getHelper(context);
        }
        return databaseHelper;
    }

    public void close(){
        if(DZSYSDICEntityDaoOpe!=null){
            DZSYSDICEntityDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(DZSYSDICEntity entity){
        try{
            DZSYSDICEntityDaoOpe.create(entity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<DZSYSDICEntity> queryAll(){
        try{
            return DZSYSDICEntityDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<DZSYSDICEntity> queryByType(String type){
        try{
            return DZSYSDICEntityDaoOpe.queryBuilder().where().eq("TYPE", type).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isEntityExist(String name){
        try{
            List<DZSYSDICEntity> rslt_List=new ArrayList<>();
            rslt_List=DZSYSDICEntityDaoOpe.queryBuilder().where().eq("NAME", name).query();
            if(rslt_List!=null && rslt_List.size()>0){
                return true;
            }else{
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
