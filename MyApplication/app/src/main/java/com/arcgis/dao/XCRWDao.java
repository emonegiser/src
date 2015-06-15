package com.arcgis.dao;

import android.content.Context;

import com.arcgis.dbutil.DatabaseHelper;
import com.arcgis.entity.XCRWEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mars on 2015/2/12.
 */
public class XCRWDao {

    private Dao<XCRWEntity, Integer> XCRWEntityDaoOpe=null;
    private DatabaseHelper databaseHelper=null;
    private Context context;


    public XCRWDao(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            XCRWEntityDaoOpe = databaseHelper.getDao(XCRWEntity.class);
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
        if(XCRWEntityDaoOpe!=null){
            XCRWEntityDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(XCRWEntity entity){
        try{
            XCRWEntityDaoOpe.create(entity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public List<XCRWEntity> queryListById(String Id){
        try{
            return XCRWEntityDaoOpe.queryBuilder().where().eq("RWBH", Id).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isExistEntity(String Id){
        try{
//            List<XCRWEntity> cbyd_list=XCRWEntityDaoOpe.queryBuilder().where().eq("RWBH", Id).query();
            List<XCRWEntity> cbyd_list=XCRWEntityDaoOpe.queryBuilder().where().eq("RWBH",Id).query();
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
    public int updateCBYDEntity(XCRWEntity entity){
        try {
            Dao.CreateOrUpdateStatus createOrUpdateStatus=XCRWEntityDaoOpe.createOrUpdate(entity);
            return createOrUpdateStatus.getNumLinesChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<XCRWEntity> queryAll(){
        try{
            return XCRWEntityDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
