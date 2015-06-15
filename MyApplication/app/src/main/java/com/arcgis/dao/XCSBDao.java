package com.arcgis.dao;

import android.content.Context;

import com.arcgis.dbutil.DatabaseHelper;
import com.arcgis.entity.XCSBEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mars on 2015/2/12.
 */
public class XCSBDao {

    private Dao<XCSBEntity, Integer> XCSBEntityDaoOpe=null;
    private DatabaseHelper databaseHelper=null;
    private Context context;

    public XCSBDao(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            XCSBEntityDaoOpe = databaseHelper.getDao(XCSBEntity.class);
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
        if(XCSBEntityDaoOpe!=null){
            XCSBEntityDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(XCSBEntity entity){
        try{
            XCSBEntityDaoOpe.create(entity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public List<XCSBEntity> queryListById(String Id){
        try{
            return XCSBEntityDaoOpe.queryBuilder().where().eq("RWBH", Id).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isExistEntity(String Id){
        try{
            List<XCSBEntity> cbyd_list=XCSBEntityDaoOpe.queryBuilder().where().eq("RWBH", Id).query();
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
    public int updateXCSBEntity(XCSBEntity entity){
        try {
            Dao.CreateOrUpdateStatus createOrUpdateStatus=XCSBEntityDaoOpe.createOrUpdate(entity);
            return createOrUpdateStatus.getNumLinesChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<XCSBEntity> queryAll(){
        try{
            return XCSBEntityDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
