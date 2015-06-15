package com.arcgis.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mars on 2015/1/29.
 * 卫片执法
 */
@DatabaseTable(tableName = "TB_WPZF")
public class WPZFEntity implements Serializable {

    /**
     * [{"ID":5,"SENDERID":"2323","SENDERNAME":"李华","RECIVERID":"hf1111,zj,2323","RECIVERNAME":"华峰,zj,李华",
     * "WPURLID":"1,5,4","XZ":"阿市乡,对坡镇,观音桥","STATE":"已完成","TASKREMARK":"阿萨德发送到",
     * "TASKDATE":"\/Date(1424016000000)\/","RESULTDATE":"\/Date(1423238400000)\/","RESULTREMARK":"adfad",
     * "RESULTFILES":"ArcGIS API for JS开发教程.pdf"}]
     */

    @DatabaseField(generatedId = true,columnName = "ID",useGetSet = true)
    private int id;
    @DatabaseField(columnName = "IDSERVER",useGetSet = true)
    private String ID;
    @DatabaseField(columnName = "SENDERID",useGetSet = true)
    private String SENDERID;
    @DatabaseField(columnName = "SENDERNAME",useGetSet = true)
    private String SENDERNAME;
    @DatabaseField(columnName = "RECIVERID",useGetSet = true)
    private String RECIVERID;
    @DatabaseField(columnName = "RECIVERNAME",useGetSet = true)
    private String RECIVERNAME;
    @DatabaseField(columnName = "WPURLID",useGetSet = true)
    private String WPURLID;
    @DatabaseField(columnName = "XZ",useGetSet = true)
    private String XZ;
    @DatabaseField(columnName = "STATE",useGetSet = true)
    private String STATE;
    @DatabaseField(columnName = "TASKREMARK",useGetSet = true)
    private String TASKREMARK;
    @DatabaseField(columnName = "TASKDATE",useGetSet = true)
    private String TASKDATE;
    @DatabaseField(columnName = "RESULTDATE",useGetSet = true)
    private String RESULTDATE;
    @DatabaseField(columnName = "RESULTREMARK",useGetSet = true)
    private String RESULTREMARK;
    @DatabaseField(columnName = "RESULTFILES",useGetSet = true)
    private String RESULTFILES;

    public WPZFEntity() {
    }

    @Override
    public String toString() {
        return "WPZFEntity{" +
                "id=" + id +
                ", ID='" + ID + '\'' +
                ", SENDERID='" + SENDERID + '\'' +
                ", SENDERNAME='" + SENDERNAME + '\'' +
                ", RECIVERID='" + RECIVERID + '\'' +
                ", RECIVERNAME='" + RECIVERNAME + '\'' +
                ", WPURLID='" + WPURLID + '\'' +
                ", XZ='" + XZ + '\'' +
                ", STATE='" + STATE + '\'' +
                ", TASKREMARK='" + TASKREMARK + '\'' +
                ", TASKDATE='" + TASKDATE + '\'' +
                ", RESULTDATE='" + RESULTDATE + '\'' +
                ", RESULTREMARK='" + RESULTREMARK + '\'' +
                ", RESULTFILES='" + RESULTFILES + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRESULTFILES() {
        return RESULTFILES;
    }

    public void setRESULTFILES(String RESULTFILES) {
        this.RESULTFILES = RESULTFILES;
    }

    public String getSTATE() {
        return STATE;
    }

    public void setSTATE(String STATE) {
        this.STATE = STATE;
    }

    public String getSENDERID() {
        return SENDERID;
    }

    public void setSENDERID(String SENDERID) {
        this.SENDERID = SENDERID;
    }

    public String getSENDERNAME() {
        return SENDERNAME;
    }

    public void setSENDERNAME(String SENDERNAME) {
        this.SENDERNAME = SENDERNAME;
    }

    public String getRECIVERID() {
        return RECIVERID;
    }

    public void setRECIVERID(String RECIVERID) {
        this.RECIVERID = RECIVERID;
    }

    public String getRECIVERNAME() {
        return RECIVERNAME;
    }

    public void setRECIVERNAME(String RECIVERNAME) {
        this.RECIVERNAME = RECIVERNAME;
    }

    public String getWPURLID() {
        return WPURLID;
    }

    public void setWPURLID(String WPURLID) {
        this.WPURLID = WPURLID;
    }

    public String getXZ() {
        return XZ;
    }

    public void setXZ(String XZ) {
        this.XZ = XZ;
    }

    public String getTASKREMARK() {
        return TASKREMARK;
    }

    public void setTASKREMARK(String TASKREMARK) {
        this.TASKREMARK = TASKREMARK;
    }

    public String getTASKDATE() {
        return TASKDATE;
    }

    public void setTASKDATE(String TASKDATE) {
        this.TASKDATE = TASKDATE;
    }

    public String getRESULTDATE() {
        return RESULTDATE;
    }

    public void setRESULTDATE(String RESULTDATE) {
        this.RESULTDATE = RESULTDATE;
    }

    public String getRESULTREMARK() {
        return RESULTREMARK;
    }

    public void setRESULTREMARK(String RESULTREMARK) {
        this.RESULTREMARK = RESULTREMARK;
    }
}
