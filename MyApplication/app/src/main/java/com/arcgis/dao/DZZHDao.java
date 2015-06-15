package com.arcgis.dao;

import android.content.Context;

import com.arcgis.dbutil.DatabaseHelper;
import com.arcgis.entity.DZZHEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mars on 2015/2/12.
 * update by pangcongcong 2015/5/30
 */
public class DZZHDao {

    private Dao<DZZHEntity, Integer> DZZHEntityDaoOpe=null;
    private DatabaseHelper databaseHelper=null;
    private Context context;


    public DZZHDao(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            DZZHEntityDaoOpe = databaseHelper.getDao(DZZHEntity.class);
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
        if(DZZHEntityDaoOpe!=null){
            DZZHEntityDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(DZZHEntity dzzhEntity){
        try{
            DZZHEntityDaoOpe.create(dzzhEntity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public DZZHEntity getDZZHEntity(int id){
        DZZHEntity dzzhEntity = null;
        try{
            dzzhEntity = DZZHEntityDaoOpe.queryForId(id);

        } catch (SQLException e){
            e.printStackTrace();
        }
        return dzzhEntity;
    }


    public boolean deleteDZZH(int id)
    {
        try {
//            DZZHEntityDaoOpe.deleteBuilder().where().eq("idd",id);
            DZZHEntityDaoOpe.deleteById(id);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        if (queryListById(id)!=null) {
            return true;
        }
        return false;
    }

    public List<DZZHEntity> queryListById(int Id){
        try{
            return DZZHEntityDaoOpe.queryBuilder().where().eq("idd", Id).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    //查询当前操作时所有的ID
    public  List<Integer> QueryIds()
    {
        List<Integer> listIds=new ArrayList<Integer>();
        List<DZZHEntity> dzzhEntities=queryAll();
        if (dzzhEntities!=null) {
            for (DZZHEntity dzzhEntity : dzzhEntities) {
                Integer id = dzzhEntity.getIdd();
                try
                {
                    listIds.add(id);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return  listIds;
    }

    public List<DZZHEntity> queryAll(){
        try{
            return DZZHEntityDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
