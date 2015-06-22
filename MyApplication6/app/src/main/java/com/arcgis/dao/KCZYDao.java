package com.arcgis.dao;

import android.content.Context;

import com.arcgis.dbutil.DatabaseHelper;
import com.arcgis.entity.KCZYEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by mars on 2015/2/12.
 */
public class KCZYDao {

    private Dao<KCZYEntity, Integer> KCZYEntityDaoOpe=null;
    private DatabaseHelper databaseHelper=null;
    private Context context;

    public KCZYDao(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            KCZYEntityDaoOpe = databaseHelper.getDao(KCZYEntity.class);
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
        if(KCZYEntityDaoOpe!=null){
            KCZYEntityDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(KCZYEntity kczyEntity){
        try{
            KCZYEntityDaoOpe.create(kczyEntity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public KCZYEntity getDZZHEntity(int id){
        KCZYEntity kczyEntity = null;
        try{
            kczyEntity = KCZYEntityDaoOpe.queryForId(id);

        } catch (SQLException e){
            e.printStackTrace();
        }
        return kczyEntity;
    }

    public List<KCZYEntity> queryListById(int Id){
        try{
            return KCZYEntityDaoOpe.queryBuilder().where().eq("user_id", Id).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<KCZYEntity> queryAll(){
        try{
            return KCZYEntityDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
