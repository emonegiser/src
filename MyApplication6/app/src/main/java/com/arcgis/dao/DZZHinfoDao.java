package com.arcgis.dao;

import android.content.Context;
import android.os.Build;

import com.arcgis.dbutil.DatabaseHelper;
import com.arcgis.entity.DZZHinfoEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pangcongcong on 2015/5/29.
 */
public class DZZHinfoDao {
    private Dao<DZZHinfoEntity, Integer> DZZHEntityinfoDaoOpe=null;
    private DatabaseHelper databaseHelper=null;
    private Context context;

    public DZZHinfoDao(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            DZZHEntityinfoDaoOpe = databaseHelper.getDao(DZZHinfoEntity.class);
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
        if(DZZHEntityinfoDaoOpe!=null){
            DZZHEntityinfoDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(DZZHinfoEntity dzzhinfoEntity){
        try{
            DZZHEntityinfoDaoOpe.create(dzzhinfoEntity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public DZZHinfoEntity getDZZHEntityinfo(int id){
        DZZHinfoEntity dzzhinfoEntity = null;
        try{
            dzzhinfoEntity = DZZHEntityinfoDaoOpe.queryForId(id);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return dzzhinfoEntity;
    }


    public boolean deleteDZZHinfo(int id)
    {
        try {
//            DZZHEntityinfoDaoOpe.deleteBuilder().where().eq("idd",id);
            DZZHEntityinfoDaoOpe.deleteById(id);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        if (queryListById(id)!=null) {
            return true;
        }
        return false;
    }

    public List<DZZHinfoEntity> queryListById(int Id){
        try{
            return DZZHEntityinfoDaoOpe.queryBuilder().where().eq("idd", Id).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    //查询当前操作时所有的ID
    public  List<Integer> QueryIds()
    {
        List<Integer> listIds=new ArrayList<Integer>();
        List<DZZHinfoEntity> dzzhinfoEntities=queryAll();
        if (dzzhinfoEntities!=null) {
            for (DZZHinfoEntity dzzhinfoEntity : dzzhinfoEntities) {
                Integer id = dzzhinfoEntity.getIdd();
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

    public List<DZZHinfoEntity> queryAll(){
        try{
            return DZZHEntityinfoDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
