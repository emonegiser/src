package com.arcgis.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mars on 2015/1/29.
 * 储备用地
 */

public class DZZHYJEntity implements Serializable {
//private

//    {"ID":"2","TB_DisasterWarn_ID":"222","ZHMC":"滑坡","XXWZ":"川山村枷担弯组","ZHDJ":"三级","BJRQ":"2014/3/31 0:00:00",
// "BZ":"紧急救援","IMAGES":"","X":"35532393.674099","Y":"3059007.567875","E":"105.328257","N":"27.643748"}
private  String ID;
    private String TB_DisasterWarn_ID;
    private String ZHMC;
    private String XXWZ;
    private  String ZHDJ;
    private  String BJRQ;
    private String BZ;
    private String IMAGES="NO";
    private String X;
    private String Y;
    private String E;
    private String N;
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTB_DisasterWarn_ID() {
        return TB_DisasterWarn_ID;
    }

    public void setTB_DisasterWarn_ID(String TB_DisasterWarn_ID) {
        this.TB_DisasterWarn_ID = TB_DisasterWarn_ID;
    }

    public String getZHMC() {
        return ZHMC;
    }

    public void setZHMC(String ZHMC) {
        this.ZHMC = ZHMC;
    }

    public String getXXWZ() {
        return XXWZ;
    }

    public void setXXWZ(String XXWZ) {
        this.XXWZ = XXWZ;
    }

    public String getZHDJ() {
        return ZHDJ;
    }

    public void setZHDJ(String ZHDJ) {
        this.ZHDJ = ZHDJ;
    }

    public String getBJRQ() {
        return BJRQ;
    }

    public void setBJRQ(String BJRQ) {
        this.BJRQ = BJRQ;
    }

    public String getBZ() {
        return BZ;
    }

    public void setBZ(String BZ) {
        this.BZ = BZ;
    }

    public String getIMAGES() {
        return IMAGES;
    }

    public void setIMAGES(String IMAGES) {
        this.IMAGES = IMAGES;
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

    public String getE() {
        return E;
    }

    public void setE(String e) {
        E = e;
    }

    public String getN() {
        return N;
    }

    public void setN(String n) {
        N = n;
    }

}
