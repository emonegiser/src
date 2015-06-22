package com.arcgis.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mars on 2015/1/29.
 * 巡查任务
 */
@DatabaseTable(tableName = "TB_XCRW")
public class XCRWEntity implements Serializable {

    /**
     * [{"ID":21,"RWBH":"130676043871617756","SENDER_ID":"zj","RECEIVER_ID":"2323",
     * "SENDTIME":"\/Date(1423065600000)\/","TASKTITLE":"werwe","TASKCONTENT":"erter",
     * "X":35550397.04379700,"Y":3054022.68020308,"E":105.51047862,"N":27.59821317,"TASKAddress":"亮岩镇,飞轮村",
     * "TASKFILES":"","RESULTCONTENT":"","RESULTFILES":"arcengine10.1开发总结.docx","STATE":"已完成",
     * "COMPLETETIME":"2015/2/6 17:43:10","TASKTYPE":"灾害点巡查"}]
     */

    @DatabaseField(generatedId = true,columnName = "ID",useGetSet = true)
    private int id;
    @DatabaseField(columnName = "IDSERVER",useGetSet = true)
    private String ID;
    @DatabaseField(columnName = "RWBH",useGetSet = true)
    private String RWBH;
    @DatabaseField(columnName = "SENDER_ID",useGetSet = true)
    private String SENDER_ID;
    @DatabaseField(columnName = "RECEIVER_ID",useGetSet = true)
    private String RECEIVER_ID;
    @DatabaseField(columnName = "SENDTIME",useGetSet = true)
    private String SENDTIME;
    @DatabaseField(columnName = "TASKTITLE",useGetSet = true)
    private String TASKTITLE;
    @DatabaseField(columnName = "TASKCONTENT",useGetSet = true)
    private String TASKCONTENT;
    @DatabaseField(columnName = "TASKAddress",useGetSet = true)
    private String TASKAddress;
    @DatabaseField(columnName = "TASKFILES",useGetSet = true)
    private String TASKFILES;
    @DatabaseField(columnName = "RESULTCONTENT",useGetSet = true)
    private String RESULTCONTENT;
    @DatabaseField(columnName = "RESULTFILES",useGetSet = true)
    private String RESULTFILES;
    @DatabaseField(columnName = "STATE",useGetSet = true)
    private String STATE;
    @DatabaseField(columnName = "COMPLETETIME",useGetSet = true)
    private String COMPLETETIME;
    @DatabaseField(columnName = "TASKTYPE",useGetSet = true)
    private String TASKTYPE;
    @DatabaseField(columnName = "E",useGetSet = true)
    private String E;
    @DatabaseField(columnName = "N",useGetSet = true)
    private String N;
    @DatabaseField(columnName = "X",useGetSet = true)
    private String X;
    @DatabaseField(columnName = "Y",useGetSet = true)
    private String Y;

    @Override
    public String toString() {
        return "XCRWEntity{" +
                "id=" + id +
                ", ID='" + ID + '\'' +
                ", RWBH='" + RWBH + '\'' +
                ", SENDER_ID='" + SENDER_ID + '\'' +
                ", RECEIVER_ID='" + RECEIVER_ID + '\'' +
                ", SENDTIME='" + SENDTIME + '\'' +
                ", TASKTITLE='" + TASKTITLE + '\'' +
                ", TASKCONTENT='" + TASKCONTENT + '\'' +
                ", TASKAddress='" + TASKAddress + '\'' +
                ", TASKFILES='" + TASKFILES + '\'' +
                ", RESULTCONTENT='" + RESULTCONTENT + '\'' +
                ", RESULTFILES='" + RESULTFILES + '\'' +
                ", STATE='" + STATE + '\'' +
                ", COMPLETETIME='" + COMPLETETIME + '\'' +
                ", TASKTYPE='" + TASKTYPE + '\'' +
                ", E='" + E + '\'' +
                ", N='" + N + '\'' +
                ", X='" + X + '\'' +
                ", Y='" + Y + '\'' +
                '}';
    }

    public XCRWEntity() {
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

    public String getRWBH() {
        return RWBH;
    }

    public void setRWBH(String RWBH) {
        this.RWBH = RWBH;
    }

    public String getSENDER_ID() {
        return SENDER_ID;
    }

    public void setSENDER_ID(String SENDER_ID) {
        this.SENDER_ID = SENDER_ID;
    }

    public String getRECEIVER_ID() {
        return RECEIVER_ID;
    }

    public void setRECEIVER_ID(String RECEIVER_ID) {
        this.RECEIVER_ID = RECEIVER_ID;
    }

    public String getSENDTIME() {
        return SENDTIME;
    }

    public void setSENDTIME(String SENDTIME) {
        this.SENDTIME = SENDTIME;
    }

    public String getTASKTITLE() {
        return TASKTITLE;
    }

    public void setTASKTITLE(String TASKTITLE) {
        this.TASKTITLE = TASKTITLE;
    }

    public String getTASKCONTENT() {
        return TASKCONTENT;
    }

    public void setTASKCONTENT(String TASKCONTENT) {
        this.TASKCONTENT = TASKCONTENT;
    }

    public String getTASKAddress() {
        return TASKAddress;
    }

    public void setTASKAddress(String TASKAddress) {
        this.TASKAddress = TASKAddress;
    }

    public String getTASKFILES() {
        return TASKFILES;
    }

    public void setTASKFILES(String TASKFILES) {
        this.TASKFILES = TASKFILES;
    }

    public String getRESULTCONTENT() {
        return RESULTCONTENT;
    }

    public void setRESULTCONTENT(String RESULTCONTENT) {
        this.RESULTCONTENT = RESULTCONTENT;
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

    public String getCOMPLETETIME() {
        return COMPLETETIME;
    }

    public void setCOMPLETETIME(String COMPLETETIME) {
        this.COMPLETETIME = COMPLETETIME;
    }

    public String getTASKTYPE() {
        return TASKTYPE;
    }

    public void setTASKTYPE(String TASKTYPE) {
        this.TASKTYPE = TASKTYPE;
    }

    public String getE() {
        return E;
    }

    public void setE(String e) {
        this.E = e;
    }

    public String getN() {
        return N;
    }

    public void setN(String n) {
        this.N = n;
    }

    public String getX() {
        return X;
    }

    public void setX(String x) {
        this.X = x;
    }

    public String getY() {
        return Y;
    }

    public void setY(String y) {
        this.Y = y;
    }
}
