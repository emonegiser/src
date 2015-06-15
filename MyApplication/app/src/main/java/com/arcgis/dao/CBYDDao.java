package com.arcgis.dao;

import android.content.Context;

import com.arcgis.dbutil.DatabaseHelper;
import com.arcgis.entity.CBYDEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mars on 2015/2/12.
 */
public class CBYDDao {

    private Dao<CBYDEntity, Integer> CBYDEntityDaoOpe=null;
    private DatabaseHelper databaseHelper=null;
    private Context context;


    public CBYDDao(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            CBYDEntityDaoOpe = databaseHelper.getDao(CBYDEntity.class);
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
        if(CBYDEntityDaoOpe!=null){
            CBYDEntityDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(CBYDEntity entity){
        try{
            CBYDEntityDaoOpe.create(entity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public List<CBYDEntity> queryListById(String Id){
        try{
            return CBYDEntityDaoOpe.queryBuilder().where().eq("BH", Id).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isExistEntity(String Id){
        try{
            List<CBYDEntity> cbyd_list=CBYDEntityDaoOpe.queryBuilder().where().eq("BH", Id).query();
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
    public int updateCBYDEntity(CBYDEntity entity){
        try {
            Dao.CreateOrUpdateStatus createOrUpdateStatus=CBYDEntityDaoOpe.createOrUpdate(entity);
            return createOrUpdateStatus.getNumLinesChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<CBYDEntity> queryAll(){
        try{
            return CBYDEntityDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
