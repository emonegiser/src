package com.arcgis.dao;

import android.content.Context;

import com.arcgis.dbutil.DatabaseHelper;
import com.arcgis.entity.LoginEntity;
import com.arcgis.entity.XCRYEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by EMonegiser on 2015/6/14.
 */
public class LoginDao {
    private Dao<LoginEntity, Integer> LoginDaoOpe=null;
    private DatabaseHelper databaseHelper=null;
    private Context context;

    public LoginDao(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            LoginDaoOpe = databaseHelper.getDao(LoginEntity.class);
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
        if(LoginDaoOpe!=null){
            LoginDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(LoginEntity loginEntity){
        try{
            LoginDaoOpe.create(loginEntity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public LoginEntity getXCRY(int id){
        LoginEntity loginEntity = null;
        try{
            loginEntity = LoginDaoOpe.queryForId(id);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return loginEntity;
    }


    public boolean DeleteLogins(int id)
    {
        try {
//            DZZHEntityinfoDaoOpe.deleteBuilder().where().eq("idd",id);
            LoginDaoOpe.deleteById(id);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        if (queryListById(id)!=null) {
            return true;
        }
        return false;
    }

    public void deleteAll()
    {
        List<Integer> ids=QueryIds();
        for (Integer id:ids)
        {
            DeleteLogins(id);
        }
    }

    public List<LoginEntity> queryListById(int ObjID){
        try{
            return LoginDaoOpe.queryBuilder().where().eq("ObjID", ObjID).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    //??????????????§Ö?ID
    public  List<Integer> QueryIds()
    {
        List<Integer> listIds=new ArrayList<Integer>();
        List<LoginEntity> loginEntityList=queryAll();
        if (loginEntityList!=null) {
            for (LoginEntity loginEntity : loginEntityList) {
                Integer id = loginEntity.getId();
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

    public List<LoginEntity> queryAll(){
        try{
            return LoginDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    //?????????????????????????
    public List<String> QueryLoginAccount(String objid)
    {
        List<String> LoginNames=new ArrayList<String>();
        try{
            List<LoginEntity> loginEntities= LoginDaoOpe.queryBuilder().where().eq("ObjID", objid).query();
            for (LoginEntity entity:loginEntities)
            {
                LoginNames.add(entity.getObjID());
            }
            return LoginNames;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

}
