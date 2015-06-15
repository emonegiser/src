package com.arcgis.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mars on 2015/1/29.
 * 上报用地
 */
@DatabaseTable(tableName = "TB_SBYD")
public class SBYDEntity implements Serializable {
    //"OBJECTID":3275,"BH":"6938029","DKZL":"1","DKMJ":14233378.73254800,"ZDBCFY":"123",
    // "DSFZWBCF":"2343","FFID":221,"SSSJ":null,"X":105.14485067,"Y":27.16151069,"QSLB":null,
    // "BSSJ":"\/Date(1421769600000)\/","PFSJ":null,"DJSJ":null,"TDZSMC":"ddasda","TDZSWH":null,
    // "CUN":"法朗村","XZ":"千溪乡","DTTF":"G48 G 020051,G48 G 020050,G48 G 021051","Shape":3212
    @DatabaseField(generatedId = true,columnName = "ID",useGetSet = true)
    private int id;
    @DatabaseField(columnName = "OBJECTID",useGetSet = true)
    private String OBJECTID;
    @DatabaseField(columnName = "BH",useGetSet = true)
    private String BH;
    @DatabaseField(columnName = "DKZL",useGetSet = true)
    private String DKZL;
    @DatabaseField(columnName = "DKMJ",useGetSet = true)
    private String DKMJ;
    @DatabaseField(columnName = "ZDBCFY",useGetSet = true)
    private String ZDBCFY;
    @DatabaseField(columnName = "DSFZWBCF",useGetSet = true)
    private String DSFZWBCF;
    @DatabaseField(columnName = "FFID",useGetSet = true)
    private String FFID;
    @DatabaseField(columnName = "SSSJ",useGetSet = true)
    private String SSSJ;
    @DatabaseField(columnName = "X",useGetSet = true)
    private String X;
    @DatabaseField(columnName = "Y",useGetSet = true)
    private String Y;
    @DatabaseField(columnName = "QSLB",useGetSet = true)
    private String QSLB;
    @DatabaseField(columnName = "BSSJ",useGetSet = true)
    private String BSSJ;
    @DatabaseField(columnName = "DJSJ",useGetSet = true)
    private String DJSJ;
    @DatabaseField(columnName = "PFSJ",useGetSet = true)
    private String PFSJ;
    @DatabaseField(columnName = "TDZSMC",useGetSet = true)
    private String TDZSMC;
    @DatabaseField(columnName = "TDZSWH",useGetSet = true)
    private String TDZSWH;
    @DatabaseField(columnName = "CUN",useGetSet = true)
    private String CUN;
    @DatabaseField(columnName = "XZ",useGetSet = true)
    private String XZ;
    @DatabaseField(columnName = "DTTF",useGetSet = true)
    private String DTTF;
    @DatabaseField(columnName = "Shape",useGetSet = true)
    private String Shape;
    @DatabaseField(columnName = "COORDS",useGetSet = true)
    private String coords;

    public SBYDEntity() {
    }

    @Override
    public String toString() {
        return "SBYDEntity{" +
                "id=" + id +
                ", OBJECTID='" + OBJECTID + '\'' +
                ", BH='" + BH + '\'' +
                ", DKZL='" + DKZL + '\'' +
                ", DKMJ='" + DKMJ + '\'' +
                ", ZDBCFY='" + ZDBCFY + '\'' +
                ", DSFZWBCF='" + DSFZWBCF + '\'' +
                ", FFID='" + FFID + '\'' +
                ", SSSJ='" + SSSJ + '\'' +
                ", X='" + X + '\'' +
                ", Y='" + Y + '\'' +
                ", QSLB='" + QSLB + '\'' +
                ", BSSJ='" + BSSJ + '\'' +
                ", DJSJ='" + DJSJ + '\'' +
                ", PFSJ='" + PFSJ + '\'' +
                ", TDZSMC='" + TDZSMC + '\'' +
                ", TDZSWH='" + TDZSWH + '\'' +
                ", CUN='" + CUN + '\'' +
                ", XZ='" + XZ + '\'' +
                ", DTTF='" + DTTF + '\'' +
                ", Shape='" + Shape + '\'' +
                ", coords='" + coords + '\'' +
                '}';
    }

    public String getCoords() {
        return coords;
    }

    public void setCoords(String coords) {
        this.coords = coords;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOBJECTID() {
        return OBJECTID;
    }

    public void setOBJECTID(String OBJECTID) {
        this.OBJECTID = OBJECTID;
    }

    public String getBH() {
        return BH;
    }

    public void setBH(String BH) {
        this.BH = BH;
    }

    public String getDKZL() {
        return DKZL;
    }

    public void setDKZL(String DKZL) {
        this.DKZL = DKZL;
    }

    public String getDKMJ() {
        return DKMJ;
    }

    public void setDKMJ(String DKMJ) {
        this.DKMJ = DKMJ;
    }

    public String getZDBCFY() {
        return ZDBCFY;
    }

    public void setZDBCFY(String ZDBCFY) {
        this.ZDBCFY = ZDBCFY;
    }

    public String getDSFZWBCF() {
        return DSFZWBCF;
    }

    public void setDSFZWBCF(String DSFZWBCF) {
        this.DSFZWBCF = DSFZWBCF;
    }

    public String getFFID() {
        return FFID;
    }

    public void setFFID(String FFID) {
        this.FFID = FFID;
    }

    public String getSSSJ() {
        return SSSJ;
    }

    public void setSSSJ(String SSSJ) {
        this.SSSJ = SSSJ;
    }

    public String getX() {
        return X;
    }

    public void setX(String x) {
        X = x;
    }

    public String getY() {
        return Y;
    }

    public void setY(String y) {
        Y = y;
    }

    public String getQSLB() {
        return QSLB;
    }

    public void setQSLB(String QSLB) {
        this.QSLB = QSLB;
    }

    public String getBSSJ() {
        return BSSJ;
    }

    public void setBSSJ(String BSSJ) {
        this.BSSJ = BSSJ;
    }

    public String getPFSJ() {
        return PFSJ;
    }

    public void setPFSJ(String PFSJ) {
        this.PFSJ = PFSJ;
    }

    public String getDJSJ() {
        return DJSJ;
    }

    public void setDJSJ(String DJSJ) {
        this.DJSJ = DJSJ;
    }

    public String getTDZSMC() {
        return TDZSMC;
    }

    public void setTDZSMC(String TDZSMC) {
        this.TDZSMC = TDZSMC;
    }

    public String getTDZSWH() {
        return TDZSWH;
    }

    public void setTDZSWH(String TDZSWH) {
        this.TDZSWH = TDZSWH;
    }

    public String getCUN() {
        return CUN;
    }

    public void setCUN(String CUN) {
        this.CUN = CUN;
    }

    public String getXZ() {
        return XZ;
    }

    public void setXZ(String XZ) {
        this.XZ = XZ;
    }

    public String getDTTF() {
        return DTTF;
    }

    public void setDTTF(String DTTF) {
        this.DTTF = DTTF;
    }

    public String getShape() {
        return Shape;
    }

    public void setShape(String shape) {
        Shape = shape;
    }
}
