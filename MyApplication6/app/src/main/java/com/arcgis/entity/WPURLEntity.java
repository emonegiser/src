package com.arcgis.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mars on 2015/1/29.
 * 卫片详情
 */
@DatabaseTable(tableName = "TB_WPDETAIL")
public class WPURLEntity implements Serializable {
    /**
     * [,{"ID":1,"XZ":"阿市乡","MAPURL":"http://localhost:6080/arcgis/rest/services/BJS_XZQH/MapServer",
     * "FILES":"Beetl目前版本是2介绍.docx","REMARK":"qwe","DATE":"\/Date(1423152000000)\/"}]
     */
    @DatabaseField(generatedId = true,columnName = "ID",useGetSet = true)
    private int id;
    @DatabaseField(columnName = "IDSERVER",useGetSet = true)
    private String ID;
    @DatabaseField(columnName = "MAPURL",useGetSet = true)
    private String MAPURL;
    @DatabaseField(columnName = "FILES",useGetSet = true)
    private String FILES;
    @DatabaseField(columnName = "REMARK",useGetSet = true)
    private String REMARK;
    @DatabaseField(columnName = "DATE",useGetSet = true)
    private String DATE;
    @DatabaseField(columnName = "XZ",useGetSet = true)
    private String XZ;

    public WPURLEntity() {
    }

    @Override
    public String toString() {
        return "WPURLEntity{" +
                "id=" + id +
                ", ID='" + ID + '\'' +
                ", MAPURL='" + MAPURL + '\'' +
                ", FILES='" + FILES + '\'' +
                ", REMARK='" + REMARK + '\'' +
                ", DATE='" + DATE + '\'' +
                ", XZ='" + XZ + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMAPURL() {
        return MAPURL;
    }

    public void setMAPURL(String MAPURL) {
        this.MAPURL = MAPURL;
    }

    public String getFILES() {
        return FILES;
    }

    public void setFILES(String FILES) {
        this.FILES = FILES;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getXZ() {
        return XZ;
    }

    public void setXZ(String XZ) {
        this.XZ = XZ;
    }
}
