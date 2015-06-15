package com.arcgis.dao;

import android.content.Context;

import com.arcgis.dbutil.DatabaseHelper;
import com.arcgis.entity.WPZFEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mars on 2015/2/12.
 */
public class WPZFDao {

    private Dao<WPZFEntity, Integer> WPZFEntityDaoOpe=null;
    private DatabaseHelper databaseHelper=null;
    private Context context;


    public WPZFDao(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            WPZFEntityDaoOpe = databaseHelper.getDao(WPZFEntity.class);
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
        if(WPZFEntityDaoOpe!=null){
            WPZFEntityDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(WPZFEntity entity){
        try{
            WPZFEntityDaoOpe.create(entity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public List<WPZFEntity> queryListById(String Id){
        try{
            return WPZFEntityDaoOpe.queryBuilder().where().eq("ID", Id).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isExistEntity(String Id){
        try{
            List<WPZFEntity> cbyd_list=WPZFEntityDaoOpe.queryBuilder().where().eq("ID", Id).query();
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
    public int updateCBYDEntity(WPZFEntity entity){
        try {
            Dao.CreateOrUpdateStatus createOrUpdateStatus=WPZFEntityDaoOpe.createOrUpdate(entity);
            return createOrUpdateStatus.getNumLinesChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<WPZFEntity> queryAll(){
        try{
            return WPZFEntityDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
