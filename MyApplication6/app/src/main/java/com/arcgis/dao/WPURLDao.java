package com.arcgis.dao;

import android.content.Context;

import com.arcgis.dbutil.DatabaseHelper;
import com.arcgis.entity.WPURLEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mars on 2015/2/12.
 */
public class WPURLDao {

    private Dao<WPURLEntity, Integer> WPURLEntityDaoOpe=null;
    private DatabaseHelper databaseHelper=null;
    private Context context;

    private  final static int version=1;

    public WPURLDao(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            WPURLEntityDaoOpe = databaseHelper.getDao(WPURLEntity.class);
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
        if(WPURLEntityDaoOpe!=null){
            WPURLEntityDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(WPURLEntity entity){
        try{
            WPURLEntityDaoOpe.create(entity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public List<WPURLEntity> queryListById(String Id){
        try{
            return WPURLEntityDaoOpe.queryBuilder().where().eq("ID", Id).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isExistEntity(String Id){
        try{
            List<WPURLEntity> cbyd_list=WPURLEntityDaoOpe.queryBuilder().where().eq("ID", Id).query();
            if(cbyd_list!=null && cbyd_list.size()>0){
                return true;
            }else{
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    //更新数据
    public int updateCBYDEntity(WPURLEntity entity){
        try {
            Dao.CreateOrUpdateStatus createOrUpdateStatus=WPURLEntityDaoOpe.createOrUpdate(entity);
            return createOrUpdateStatus.getNumLinesChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<WPURLEntity> queryAll(){
        try{
            return WPURLEntityDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
