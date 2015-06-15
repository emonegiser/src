package com.arcgis.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by EMonegiser on 2015/6/14.
 */

@DatabaseTable(tableName = "TB_DZZH")
public class LoginEntity implements Serializable {


    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "ObjID",useGetSet = true)
    private String ObjID;//±‡∫≈

    @DatabaseField(columnName = "ObjID",useGetSet = true)
    private String Password;//…Ë±∏±‡∫≈

    @Override
    public String toString() {
        return "LoginEntity{" +
                "id=" + id +
                ", ObjID='" + ObjID + '\'' +
                ", Password='" + Password + '\'' +
                '}';
    }

    public String getObjID() {
        return ObjID;
    }

    public void setObjID(String objID) {
        ObjID = objID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
