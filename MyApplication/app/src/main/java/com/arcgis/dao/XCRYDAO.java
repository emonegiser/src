package com.arcgis.dao;

import android.content.Context;

import com.arcgis.dbutil.DatabaseHelper;
import com.arcgis.entity.XCRYEntity;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**Ѳ����ԱDAO
 * Created by EMonegiser on 2015/6/7.
 */
public class XCRYDAO {
    private Dao<XCRYEntity, Integer> XCRYDaoOpe=null;
    private DatabaseHelper databaseHelper=null;
    private Context context;

    private  final static int version=1;

    public XCRYDAO(Context context){
        try{
            this.context=context;
            databaseHelper = this.getHelper();
            XCRYDaoOpe = databaseHelper.getDao(XCRYEntity.class);
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
        if(XCRYDaoOpe!=null){
            XCRYDaoOpe=null;
        }
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public void add(XCRYEntity xcryEntity){
        try{
            XCRYDaoOpe.create(xcryEntity);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public XCRYEntity getXCRY(int id){
        XCRYEntity xcryEntity = null;
        try{
            xcryEntity = XCRYDaoOpe.queryForId(id);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return xcryEntity;
    }


    public boolean DeleteXCRY(int id)
    {
        try {
//            DZZHEntityinfoDaoOpe.deleteBuilder().where().eq("idd",id);
            XCRYDaoOpe.deleteById(id);
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
            DeleteXCRY(id);
        }
    }

    public List<XCRYEntity> queryListById(int Id){
        try{
            return XCRYDaoOpe.queryBuilder().where().eq("id", Id).query();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    //��ѯ��ǰ����ʱ���е�ID
    public  List<Integer> QueryIds()
    {
        List<Integer> listIds=new ArrayList<Integer>();
        List<XCRYEntity> xcryEntityList=queryAll();
        if (xcryEntityList!=null) {
            for (XCRYEntity xcryEntity : xcryEntityList) {
                Integer id = xcryEntity.getId();
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

    public List<XCRYEntity> queryAll(){
        try{
            return XCRYDaoOpe.queryForAll();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    //���ݲ������Ʋ�ѯѲ����Ա������
    public List<String> QueryXcrysByBm(String Bm)
    {
        try {
            if (XCRYDaoOpe.isTableExists()) {
                    List<String> XcryNames = new ArrayList<>();
                    try {
                        List<XCRYEntity> xcryEntities = XCRYDaoOpe.queryBuilder().where().eq("BM", Bm).query();
                        for (XCRYEntity entity : xcryEntities) {
                            XcryNames.add(entity.getName());
                        }
                        return XcryNames;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
